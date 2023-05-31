package org.solvo.web

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.*
import org.solvo.web.comments.CommentCard
import org.solvo.web.comments.CommentCardSubComments
import org.solvo.web.comments.CourseMenu
import org.solvo.web.document.History
import org.solvo.web.editor.RichText
import org.solvo.web.editor.rememberRichEditorLoadedState
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.OverlayLoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.HorizontallyDivided
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.foundation.ifThen
import kotlin.time.Duration.Companion.seconds


fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { QuestionPageViewModel() }

            val course by model.course.collectAsState(null)
            val article by model.article.collectAsState(null)
            val question by model.question.collectAsState(null)

            SolvoTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { model.menuState.switchMenuOpen() }) {
                        Icon(Icons.Filled.Menu, null)
                    }
                }
            ) {
                course?.let { article?.let { it1 -> PaperTitle(it, it1.code) } }
            }

            CourseMenu(
                model.menuState,
                onClickQuestion = { a: ArticleDownstream, q: QuestionDownstream ->
                    History.navigate {
                        question(a.course.code, a.code, q.code)
                    }
                }
            )

            LoadableContent(course == null || article == null || question == null, Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize()) {
                    QuestionPageContent(
                        course = course ?: return@LoadableContent,
                        article = article ?: return@LoadableContent,
                        question = question ?: return@LoadableContent
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionPageContent(
    course: Course,
    article: ArticleDownstream,
    question: QuestionDownstream,
) {
    HorizontallyDivided(
        left = {
            PaperView(
                questionSelectedBar = {
                    // ScrollableTab row TODO()
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState(), true)
                    ) {
                        article.questionIndexes.forEach {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(it)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                            )
                        }

                    }
                },
                onChangeLayout = {
                    // TODO change article page layout
                },
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
            val allComments = remember { generateSequence { createCommentDownstream() }.take(10).toList() }
            val pagingState = rememberExpandablePagingState(
                Int.MAX_VALUE,
                allComments,
            )
            PagingContent(
                pagingState,
                controlBar = {
                    PagingControlBar(it) {
                        FilledTonalButton(
                            onClick = {},
                            Modifier.align(Alignment.CenterStart),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = buttonContentPaddings,
                        ) {
                            Icon(Icons.Outlined.PostAdd, "Draft Answer", Modifier.fillMaxHeight())

                            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Draft Answer",
                                    Modifier.padding(start = 4.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                )
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
                    AnswersList(
                        allItems = allComments,
                        visibleIndices = visibleIndices,
                        isExpanded = isExpanded,
                        modifier = Modifier.fillMaxSize()
                            .ifThen(!isExpanded) { verticalScroll(scrollState) },
                        onClickComment = { _, item ->
                            scope.launch(start = CoroutineStart.UNDISPATCHED) { pagingState.switchExpanded() }
                            pagingState.gotoItem(item)
                        },
                        onClickCard = { _, item ->
                            scope.launch(start = CoroutineStart.UNDISPATCHED) { pagingState.switchExpanded() }
                            pagingState.gotoItem(item)
                        }
                    )
                }
            }
        }
    )
}


// when not expanded
@Composable
private fun AnswersList(
    allItems: List<CommentDownstream>,
    visibleIndices: IntRange,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onClickCard: ((index: Int, item: CommentDownstream) -> Unit)? = null,
    onClickComment: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)? = null,
) {
    Column(modifier) {
        val allItemsIndexed = remember(allItems) { allItems.withIndex() }
        for ((index, item) in allItemsIndexed) {
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

            val scope = rememberCoroutineScope()
            var animationEnabled by remember { mutableStateOf(false) }

            CommentCard(
                author = item.author,
                date = "May 05, 2023", // TODO: 2023/5/29 date
                modifier = Modifier.then(sizeModifier).ifThen(animationEnabled) {
                    animateContentSize { _: IntSize, _: IntSize ->
                        animationEnabled = false
                    }
                },
                subComments = if (isExpanded) {
                    null
                } else {
                    {
                        CommentCardSubComments(item.subComments, onClickComment = {
                            animationEnabled = true
                            scope.launch {
                                delay(0.05.seconds)
                                onClickComment?.invoke(it, item)
                            }
                        })
                    }
                },
                onClickCard = {
                    animationEnabled = true
                    scope.launch {
                        delay(0.05.seconds)
                        onClickCard?.invoke(index, item)
                    }
                }
            ) { backgroundColor ->
                CommentCardContent(item, backgroundColor, Modifier.weight(1f)) // in column card
            }
        }
    }
}

@Composable
private fun CommentCardContent(
    item: CommentDownstream,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    key(item.coid) { // redraw editor when item id changed (do not reuse)
        val loadedState = rememberRichEditorLoadedState()
        OverlayLoadableContent(
            !loadedState.isReady,
            loadingContent = { LinearProgressIndicator() }
        ) {
            RichText(
                item.content,
                modifier = modifier.heightIn(min = 64.dp).fillMaxWidth(),
                backgroundColor = backgroundColor,
                showScrollbar = false,
                onEditorLoaded = loadedState.onEditorLoaded,
                onTextUpdated = loadedState.onTextChanged,
            )
        }
    }
}

// when expanded
@Composable
private fun ExpandedAnswerCard(
    item: CommentDownstream,
    modifier: Modifier = Modifier,
) {
    CommentCard(
        item.author,
        "May 05, 2023", // TODO: 2023/5/29 date
        modifier, // show comments in side view
    ) { backgroundColor ->
        CommentCardContent(item, backgroundColor)
    }
}