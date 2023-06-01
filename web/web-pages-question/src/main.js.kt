package org.solvo.web

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.*
import org.solvo.web.comments.*
import org.solvo.web.comments.like.ThumbActions
import org.solvo.web.document.History
import org.solvo.web.editor.RichText
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.HorizontallyDivided
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.foundation.ifThen
import org.solvo.web.ui.foundation.wrapClearFocus


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
                        QuestionPageContent(
                            course = course ?: return@LoadableContent,
                            article = article ?: return@LoadableContent,
                            question = question ?: return@LoadableContent
                        )
                    }
                }
                // TODO("Enable when rick text layer is fixed.")
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
                        modifier = Modifier.horizontalScroll(rememberScrollState(), true),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        article.questionIndexes.forEach { questionCode ->
                            InputChip(
                                selected = question.code == questionCode,
                                onClick = wrapClearFocus {
                                    if (question.code != questionCode) {
                                        History.navigate { question(course.code, article.code, questionCode) }
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
            val allAnswers = remember { generateSequence { createCommentDownstream() }.take(10).toList() }
            val pagingState = rememberExpandablePagingState(
                Int.MAX_VALUE,
                allAnswers,
            )
            PagingContent(
                pagingState,
                controlBar = controlBar@{ expandablePagingState ->
                    PagingControlBar(
                        expandablePagingState,
                        showPagingController = expandablePagingState.isExpanded.value
                    ) {
                        FilledTonalButton(
                            onClick = wrapClearFocus { },
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
                            visibleItems.firstOrNull()?.let {
                                // TODO: 2023/6/1  view model   it.allSubCommentIds
                                CommentColumn(allAnswers)
                            }
                        },
                        initialLeftWeight = 0.618f,
                        isRightVisible = isExpanded,
                        dividerModifier = Modifier.padding(horizontal = 8.dp),
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
    onSwitchExpand: ((index: Int, item: CommentDownstream) -> Unit)? = null,
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


            LargeCommentCard(
                author = item.author,
                date = "May 05, 2023", // TODO: 2023/5/29 date
                modifier = Modifier.then(sizeModifier),
                subComments = if (isExpanded) {
                    null
                } else {
                    {
                        CommentCardSubComments(item.previewSubComments, onClickComment = {
                            onClickComment?.invoke(it, item)
                        })
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
                    ThumbActions(
                        10,
                        null,
                        {},
                        {}
                    )
                }
            ) { backgroundColor ->
                CommentCardContent(item, backgroundColor, Modifier.weight(1f)) // in column card
            }
        }
    }
}
