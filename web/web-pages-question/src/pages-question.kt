package org.solvo.web

import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filterIsInstance
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.api.communication.Course
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.utils.UserPermission
import org.solvo.web.comments.CourseMenu
import org.solvo.web.comments.subComments.CommentColumn
import org.solvo.web.comments.subComments.CommentColumnViewModel
import org.solvo.web.document.History
import org.solvo.web.editor.*
import org.solvo.web.session.currentUserHasPermission
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.*


fun main() {
    onWasmReady {
        SolvoWindow {
            val editor = rememberRichEditorState(true, showToolbar = true, fontSize = DEFAULT_RICH_EDITOR_FONT_SIZE)
            val model = remember { QuestionPageViewModel(editor) }

            val course by model.course.collectAsState(null)
            val article by model.article.collectAsState(null)
            val question by model.question.collectAsState(null)
            SolvoTopAppBar(
                additionalNavigationIcons = {
                    if (currentUserHasPermission(UserPermission.OPERATOR)) {
                        IconTextButton(
                            icon = { Icon(Icons.Outlined.Settings, null) },
                            text = { Text("Operator Settings") },
                            onClick = { model.navigateToQuestionSettings() },
                            Modifier.padding(start = 12.dp),
                            indication = rememberRipple(),
                        )
                    }
                }
            ) {
                course?.let { article?.let { it1 -> PaperTitle(it, it1.code) } }
            }
            Box {
                LoadableContent(course == null || article == null || question == null, Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxSize()) {
                        QuestionPageContent(
                            model,
                            course = course ?: return@LoadableContent,
                            article = article ?: return@LoadableContent,
                            question = question ?: return@LoadableContent,
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
    model: QuestionPageViewModel,
    course: Course,
    article: ArticleDownstream,
    question: QuestionDownstream,
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
        val isEditorVisible by model.controlBarState.isEditorVisible.collectAsState(false)

        Column {
            AnswerListControlBar(
                model,
                draftAnswerEditor = model.draftEditorState,
                question = question,
                backgroundScope = model.backgroundScope
            )

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
                val isExpanded by model.isExpanded.collectAsState(false)

                var isAddCommentEditorVisible by remember { mutableStateOf(false) }

                val allAnswers by model.allAnswers.collectAsState()
                if (allAnswers.isEmpty()) {
                    CenteredTipText {
                        Icon(Icons.Outlined.AutoAwesome, null, Modifier.padding(vertical = 2.dp))
                        Text(
                            "Be the first one who share answers and thoughts!",
                            Modifier.padding(start = 8.dp),
                            softWrap = false,
                        )
                    }
                }

                AllAnswersList(
                    model,
                    modifier = Modifier.then(
                        when {
                            isEditorVisible || isExpanded -> Modifier.width(0.dp)
                            else -> Modifier.fillMaxSize()
                        }
                    ).verticalScroll(rememberScrollState())
                ) { _, item ->
                    model.expandAnswer(item)
                }

                ExpandedAnswerContent(
                    model,
                    isAddCommentEditorVisible = isAddCommentEditorVisible,
                    setAddCommentEditorVisible = { isAddCommentEditorVisible = it },
                    modifier = Modifier.padding(bottom = 12.dp).ifThen(isEditorVisible || !isExpanded) { width(0.dp) },
                )

                @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
                if (!RichEditorLayoutSizeDebug) {
                    RichEditor(
                        Modifier.padding(bottom = 12.dp).fillMaxWidth().ifThenElse(
                            isEditorVisible,
                            then = { fillMaxHeight() },
                            `else` = { height(0.dp) }
                        ),
                        fontSize = DEFAULT_RICH_EDITOR_FONT_SIZE,
                        state = model.draftEditorState
                    )
                }
            }
        }
    },
    leftWidthRange = {
        val minLeft = maxWidth * 0.3f
        minLeft..(maxWidth - 350.dp).coerceAtLeast(minLeft)
    },
)

@Composable
private fun ExpandedAnswerContent(
    model: QuestionPageViewModel,
    isAddCommentEditorVisible: Boolean,
    setAddCommentEditorVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val addCommentEditorVisibleUpdated by rememberUpdatedState(setAddCommentEditorVisible)
    HorizontallyDivided(
        left = {
            ExpandedAnswer(
                model,
                model.expandedAnswerReady.collectAsState().value ?: return@HorizontallyDivided,
            )
        },
        right = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                val item by model.expandedAnswerReady.collectAsState()

                val commentColumnModel =
                    remember { CommentColumnViewModel(snapshotFlow { item }, model.events.filterIsInstance()) }

                DraftCommentSection(
                    isEditorVisible = isAddCommentEditorVisible,
                    showEditor = { addCommentEditorVisibleUpdated(true) }
                ) { newContent ->
                    val upstream = CommentUpstream(content = newContent)
                    model.submitComment(upstream, item?.coid ?: return@DraftCommentSection)
                    addCommentEditorVisibleUpdated(false)
                }

                val allSubCommentsFlow by commentColumnModel.allSubComments.collectAsState(emptyList())
                CommentColumn(allSubCommentsFlow)
            }
        },
        initialLeftWeight = 0.618f,
        modifier = modifier,
        dividerModifier = Modifier.padding(horizontal = 8.dp),
    )
}

