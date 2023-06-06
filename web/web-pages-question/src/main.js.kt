package org.solvo.web

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.*
import org.solvo.web.comments.*
import org.solvo.web.document.History
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.editor.RichText
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.*


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
                        val allAnswersFlow by model.allAnswers.collectAsState(emptyFlow())
                        val allAnswers by allAnswersFlow.collectAsState(listOf())
                        QuestionPageContent(
                            model.backgroundScope,
                            course = course ?: return@LoadableContent,
                            article = article ?: return@LoadableContent,
                            question = question ?: return@LoadableContent,
                            allAnswers = allAnswers,
                        )
                    }
                }
                CourseMenu(
                    model.menuState,
                    onClickQuestion = wrapClearFocus { a: ArticleDownstream, q: QuestionDownstream ->
                        History.navigate {
                            question(a.course.code, a.code, q.code)
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
) {
    HorizontallyDivided(
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
                                        History.pushState { question(course.code, article.code, questionCode) }
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
                                        backgroundScope.launch {
                                            client.comments.postAnswer(
                                                question.coid,
                                                CommentUpstream(draftAnswerEditor.contentMarkdown)
                                            )
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
                        .padding(top = 12.dp, bottom = if (isExpanded) 12.dp else 0.dp)
                        .padding(end = 12.dp, start = 12.dp)
                        .fillMaxSize()
                        .focusProperties {
                            this.canFocus = false
                        }
                        .focusable(false) // compose bug
                ) {
                    val scope = rememberCoroutineScope()

                    RichEditor(
                        Modifier.fillMaxWidth().ifThenElse(
                            isDraftAnswerEditorOpen,
                            then = { fillMaxHeight() },
                            `else` = { height(0.dp) }
                        ),
                        state = draftAnswerEditor
                    )

                    HorizontallyDivided(
                        left = {
                            val onClick: (Any?, item: CommentDownstream) -> Unit = { _, item ->
                                scope.launch(start = CoroutineStart.UNDISPATCHED) { pagingState.switchExpanded() }
                                pagingState.gotoItem(item)
                            }

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
                                DraftCommentSection(backgroundScope, pagingState)

                                visibleItems.firstOrNull()?.let {
                                    val model = remember { CommentColumnViewModel(it) }
                                    val allSubCommentsFlow by model.allSubComments.collectAsState(emptyFlow())
                                    val allSubComments by allSubCommentsFlow.collectAsState(null)
                                    CommentColumn(allSubComments ?: emptyList())
                                }
                            }
                        },
                        initialLeftWeight = 0.618f,
                        isRightVisible = isExpanded,
                        modifier = Modifier.ifThen(isDraftAnswerEditorOpen) { width(0.dp) },
                        dividerModifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
        }
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
        onClick = wrapClearFocus { pagingState.switchEditorEnable() },
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
            Icon(Icons.Outlined.FolderOff, "Editor fold", Modifier.fillMaxHeight())

            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    "Fold up editor",
                    Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun DraftCommentSection(
    backgroundScope: CoroutineScope,
    pagingState: ExpandablePagingState<CommentDownstream>
) {
    DraftCommentCard(Modifier.padding(bottom = 16.dp)) {
        var showEditor by remember { mutableStateOf(false) }
        val editorHeight by animateDpAsState(if (showEditor) 200.dp else 0.dp)

        val editorState = rememberRichEditorState(isEditable = true)
        RichEditor(
            Modifier.fillMaxWidth().height(editorHeight),
            displayMode = RichEditorDisplayMode.EDIT_ONLY,
            isToolbarVisible = false,
            state = editorState,
        )

        Button({
            if (showEditor) {
                pagingState.currentContent.value.firstOrNull()?.let { comment ->
                    backgroundScope.launch {
                        client.comments.postComment(
                            comment.coid, CommentUpstream(
                                content = editorState.contentMarkdown,
                            )
                        )
                    }
                }
            }

            showEditor = !showEditor
        }, Modifier.align(Alignment.End).animateContentSize()
            .ifThen(!showEditor) { fillMaxWidth() }
            .ifThen(showEditor) { padding(top = 12.dp).wrapContentSize() }
        ) {
            Text("Add Comment")
        }
    }
}


// when not expanded
@Composable
private fun AnswersList(
    allItems: List<CommentDownstream>,
    visibleIndices: IntRange,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onSwitchExpand: ((index: Int, item: CommentDownstream) -> Unit)? = null,
    onClickComment: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)? = null,
) {
    Column(modifier) {
        val allItemsIndexed = remember(allItems) { allItems.withIndex() }
        for ((index, item) in allItemsIndexed) {
            val viewModel = remember { FullCommentCardViewModel(item) }
            val sizeModifier = if (index in visibleIndices) {
                Modifier
                    .padding(bottom = 12.dp) // item spacing
                    .fillMaxWidth()
                    .then(
                        if (isExpanded) Modifier.fillMaxHeight() else Modifier.wrapContentHeight()
                    )
            } else {
                Modifier.requiredSize(0.dp) // `hide` item, but keep rich editor in memory (with size zero)
            }


            val postTimeFormatted by viewModel.postTimeFormatted.collectAsState(null)
            LargeCommentCard(
                author = item.author,
                date = postTimeFormatted ?: "",
                modifier = Modifier.then(sizeModifier),
                subComments = if (isExpanded) {
                    null
                } else {
                    {
                        CommentCardSubComments(
                            item.previewSubComments,
                            item.allSubCommentIds.size,
                            onClickComment = {
                                onClickComment?.invoke(it, item)
                            }
                        )
                    }
                },
                onClickCard = {
                    if (!isExpanded) {
                        onSwitchExpand?.invoke(index, item)
                    }
                },
                onClickExpand = {
                    onSwitchExpand?.invoke(index, item)
                },
                isExpand = isExpanded,
                actions = {
//                    ThumbActions(
//                        item.likes,
//                        null,
//                        {},
//                        {}
//                    )
                }
            ) { backgroundColor ->
                CommentCardContent(item, backgroundColor, Modifier.weight(1f)) // in column card
            }
        }
    }
}
