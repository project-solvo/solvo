package org.solvo.web

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.*
import org.solvo.model.api.events.Event
import org.solvo.model.utils.NonBlankString
import org.solvo.web.comments.CourseMenu
import org.solvo.web.comments.subComments.CommentColumn
import org.solvo.web.comments.subComments.CommentColumnViewModel
import org.solvo.web.document.History
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.RichText
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.*

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
internal class JsLocale(val locale: dynamic) : androidx.compose.ui.text.intl.PlatformLocale {
    override val language: String
        get() = "en"

    override val script: String
        get() = "en"

    override val region: String
        get() = "en"

    override fun toLanguageTag(): String = "en" // locale.toLanguageTag()
}


fun main() {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "VAL_REASSIGNMENT", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
    androidx.compose.ui.text.intl.platformLocaleDelegate =
        object : androidx.compose.ui.text.intl.PlatformLocaleDelegate {
            override val current: LocaleList
                get() = LocaleList(listOf(Locale(JsLocale(Any()))))


            override fun parseLanguageTag(languageTag: String): androidx.compose.ui.text.intl.PlatformLocale {
                return JsLocale(Any())
            }
        }

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
                        val allAnswersFlow by model.allAnswers.collectAsState(emptyFlow())
                        val allAnswers by allAnswersFlow.collectAsState(listOf())
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
    allAnswers: List<CommentDownstream>,
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
        val draftAnswerEditor = rememberRichEditorState(true)
        val isDraftAnswerEditorOpen by pagingState.editorEnable

        PagingContent(
            pagingState,
            controlBar = controlBar@{ expandablePagingState ->
                PagingControlBar(
                    expandablePagingState,
                    showPagingController = expandablePagingState.isExpanded.value
                ) {
                    Row(
                        Modifier.align(Alignment.CenterStart),
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        DraftAnswerButton(
                            pagingState = pagingState,
                            contentPaddings = buttonContentPaddings,
                            shape = buttonShape,
                        )

                        AnimatedVisibility(isDraftAnswerEditorOpen) {
                            Button(
                                onClick = {
                                    if (draftAnswerEditor.contentMarkdown != "") {
                                        backgroundScope.launch {
                                            client.comments.postAnswer(
                                                question.coid,
                                                CommentUpstream(
                                                    NonBlankString.fromStringOrNull(draftAnswerEditor.contentMarkdown)
                                                        ?: return@launch
                                                )
                                            )
                                        }
                                        client.refresh()
                                    }
                                },
                                shape = buttonShape,
                                contentPadding = buttonContentPaddings,
                            ) {
                                Text("Post Answer")
                            }
                        }
                    }
                }
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
                RichEditor(
                    Modifier.fillMaxWidth().ifThenElse(
                        isDraftAnswerEditorOpen,
                        then = { fillMaxHeight() },
                        `else` = { height(0.dp) }
                    ),
                    state = draftAnswerEditor,
                    fontSize = 18.sp
                )

                ExpandedAnswerContent(
                    pagingState,
                    allAnswers,
                    isExpanded,
                    backgroundScope,
                    isDraftAnswerEditorOpen,
                    events,
                )
            }
        }
    }
)

@Composable
private fun PagingContentContext<CommentDownstream>.ExpandedAnswerContent(
    pagingState: ExpandablePagingState<CommentDownstream>,
    allAnswers: List<CommentDownstream>,
    isExpanded: Boolean,
    backgroundScope: CoroutineScope,
    isDraftAnswerEditorOpen: Boolean,
    events: SharedFlow<Event>,
) {
    val scope = rememberCoroutineScope()

    var showAddCommentEditor by remember { mutableStateOf(false) }
    HorizontallyDivided(
        left = {
            val onClick: (comment: Any?, item: CommentDownstream) -> Unit = { comment, item ->
                scope.launch(start = CoroutineStart.UNDISPATCHED) { pagingState.switchExpanded() }
                pagingState.gotoItem(item)
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
                modifier = Modifier.fillMaxSize()
                    .ifThen(!isExpanded) { verticalScroll(scrollState) },
                onClickComment = onClick,
                onSwitchExpand = onClick,
            )
        },
        right = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                val visibleItems by visibleItems
                val firstItem by remember { derivedStateOf { visibleItems.firstOrNull() } }
                if (firstItem != null) {
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
            }
        },
        initialLeftWeight = 0.618f,
        isRightVisible = isExpanded,
        modifier = Modifier.ifThen(isDraftAnswerEditorOpen) { width(0.dp) },
        dividerModifier = Modifier.padding(horizontal = 8.dp),
    )
}

@Composable
private fun DraftAnswerButton(
    pagingState: ExpandablePagingState<CommentDownstream>,
    contentPaddings: PaddingValues,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = wrapClearFocus {
            if (!client.isLoginIn()) {
                client.jumpToLoginPage()
            } else {
                pagingState.switchEditorEnable()
            }
        },
        modifier,
        shape = shape,
        contentPadding = contentPaddings,
    ) {
        if (!pagingState.editorEnable.value) {
            Icon(Icons.Outlined.PostAdd, "Draft Answer", Modifier.fillMaxHeight())

            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    "Draft Answer",
                    Modifier.padding(start = 4.dp),
                )
            }
        } else {
            Icon(Icons.Outlined.FolderOff, "Fold Editor", Modifier.fillMaxHeight())

            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    "Fold Editor",
                    Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}
