package org.solvo.web

import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.Course
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.events.Event
import org.solvo.web.comments.CourseMenu
import org.solvo.web.comments.subComments.CommentColumn
import org.solvo.web.comments.subComments.CommentColumnViewModel
import org.solvo.web.document.History
import org.solvo.web.editor.*
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.*
import org.solvo.web.viewModel.LoadingUuidItem


fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { QuestionPageViewModel() }

            val course by model.course.collectAsState(null)
            val article by model.article.collectAsState(null)
            val question by model.question.collectAsState(null)
            SolvoTopAppBar(
                navigationIcon = {
                    IconButton(onClick = wrapClearFocus { model.menuState.switchMenuOpen() }) {
                        Icon(Icons.Filled.Menu, null)
                    }
                }
            ) {
                course?.let { article?.let { it1 -> PaperTitle(it, it1.code) } }
            }
            Box {
                LoadableContent(course == null || article == null || question == null, Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxSize()) {
                        val allAnswers by model.allAnswers.collectAsState(emptyList())
                        QuestionPageContent(
                            model.backgroundScope,
                            course = course ?: return@LoadableContent,
                            article = article ?: return@LoadableContent,
                            question = question ?: return@LoadableContent,
                            allAnswers = allAnswers,
                            events = model.questionEvents,
                        )
                    }
                }
                CourseMenu(
                    model.menuState,
                    onClickQuestion = wrapClearFocus { a: ArticleDownstream, q: QuestionDownstream ->
                        History.navigate {
                            question(a.course.code.str, a.code, q.code)
                        }
                    }
                )
            }


        }
    }
}

@Composable
private fun QuestionPageContent(
    backgroundScope: CoroutineScope,
    course: Course,
    article: ArticleDownstream,
    question: QuestionDownstream,
    allAnswers: List<LoadingUuidItem<CommentDownstream>>,
    events: SharedFlow<Event>,
): Unit = HorizontallyDivided(
    left = {
        PaperView(
            questionSelectedBar = {
                // ScrollableTab row TODO()
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState(), true),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    article.questionIndexes.forEach { questionCode ->
                        InputChip(
                            selected = question.code == questionCode,
                            onClick = wrapClearFocus {
                                if (question.code != questionCode) {
                                    History.pushState { question(course.code.str, article.code, questionCode) }
                                } // else: don't navigate if clicking same question
                            },
                            label = { Text(questionCode) },
                            shape = RoundedCornerShape(8.dp),
                        )
                    }

                }
            },
            onZoomIn = {},
            onZoomOut = {},
            Modifier.fillMaxSize()
        ) {
            Box(Modifier.padding(all = 12.dp)) {
                RichText(
                    question.content, Modifier.fillMaxSize(),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                )
            }
        }
    },
    right = {
        val pagingState = rememberExpandablePagingState(
            Int.MAX_VALUE,
            allAnswers,
        )
        val draftAnswerEditor =
            rememberRichEditorState(true, showToolbar = true, fontSize = DEFAULT_RICH_EDITOR_FONT_SIZE)
        val draftState = remember { DraftAnswerControlBarState() }
        val isEditorVisible by draftState.isEditorVisible.collectAsState(false)

        PagingContent(
            pagingState,
            controlBar = {
                AnswerListControlBar(
                    pagingState = pagingState,
                    model = draftState,
                    draftAnswerEditor = draftAnswerEditor,
                    question = question,
                    backgroundScope = backgroundScope
                )
            }
        ) {
            val isExpanded by pagingState.isExpanded
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .padding(end = 12.dp, start = 12.dp)
                    .fillMaxSize()
                    .focusProperties {
                        this.canFocus = false
                    }
                    .focusable(false) // compose bug
            ) {
                @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
                if (!RichEditorLayoutSizeDebug) {
                    RichEditor(
                        Modifier.fillMaxWidth().ifThenElse(
                            isEditorVisible,
                            then = { fillMaxHeight() },
                            `else` = { height(0.dp) }
                        ),
                        fontSize = DEFAULT_RICH_EDITOR_FONT_SIZE,
                        state = draftAnswerEditor
                    )
                }

                ExpandedAnswerContent(
                    pagingState = pagingState,
                    allAnswers = allAnswers,
                    isExpanded = isExpanded,
                    backgroundScope = backgroundScope,
                    isDraftAnswerEditorOpen = isEditorVisible,
                    events = events,
                )
            }
        }
    },
    leftWidthRange = {
        val minLeft = maxWidth * 0.3f
        minLeft..(maxWidth - 350.dp).coerceAtLeast(minLeft)
    },
)

private operator fun TextUnit.plus(sp: TextUnit): TextUnit {
    return (this.value + sp.value).sp
}

private operator fun TextUnit.minus(sp: TextUnit): TextUnit {
    require(this.isSp && sp.isSp)
    return (this.value - sp.value).sp
}

val CONTROL_BUTTON_FONT_SIZE = 16.sp

@Composable
private fun PagingContentContext<LoadingUuidItem<CommentDownstream>>.ExpandedAnswerContent(
    pagingState: ExpandablePagingState<LoadingUuidItem<CommentDownstream>>,
    allAnswers: List<LoadingUuidItem<CommentDownstream>>,
    isExpanded: Boolean,
    backgroundScope: CoroutineScope,
    isDraftAnswerEditorOpen: Boolean,
    events: SharedFlow<Event>,
) {
    val uiScope = rememberCoroutineScope()

    var showAddCommentEditor by remember { mutableStateOf(false) }
    HorizontallyDivided(
        left = {
            val onClick: (comment: Any?, item: CommentDownstream) -> Unit = { comment, item ->
                uiScope.launch(start = CoroutineStart.UNDISPATCHED) { pagingState.switchExpanded() }
                pagingState.gotoItemOf { it.ready?.coid == item.coid }
                if (comment == null) {
                    // clicking "Add your comment" or "See all 7 comments"
                    showAddCommentEditor = true
                }
            }

            val visibleIndices by visibleIndices
            AnswersList(
                allItems = allAnswers,
                visibleIndices = visibleIndices,
                isExpanded = isExpanded,
                events = events,
                backgroundScope = backgroundScope,
                modifier = Modifier.fillMaxSize()
                    .ifThen(!isExpanded) { verticalScroll(scrollState) },
                onClickComment = onClick,
            )
        },
        right = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                val visibleItems by visibleItems
                val firstItemLoading by remember { derivedStateOf { visibleItems.firstOrNull()?.asFlow() } }
                val firstItem by firstItemLoading?.collectAsState(null) ?: return@Column
                val model =
                    remember { CommentColumnViewModel(snapshotFlow { firstItem }, events.filterIsInstance()) }

                DraftCommentSection(
                    showEditor = showAddCommentEditor,
                    onShowEditorChange = { showAddCommentEditor = it },
                    backgroundScope = backgroundScope,
                    pagingState = pagingState,
                )

                val allSubCommentsFlow by model.allSubComments.collectAsState(emptyList())
                CommentColumn(allSubCommentsFlow)
            }
        },
        initialLeftWeight = 0.618f,
        isRightVisible = isExpanded,
        modifier = Modifier.ifThen(isDraftAnswerEditorOpen) { width(0.dp) },
        dividerModifier = Modifier.padding(horizontal = 8.dp),
    )
}

