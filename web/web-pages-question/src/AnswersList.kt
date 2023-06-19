package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentKind
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.web.answer.*
import org.solvo.web.comments.commentCard.AnswerCard
import org.solvo.web.comments.commentCard.HorizontalExpandMenu
import org.solvo.web.comments.commentCard.components.AuthorNameTextStyle
import org.solvo.web.comments.commentCard.components.CommentCardActionButton
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.FullCommentCardViewModel
import org.solvo.web.dummy.Loading
import org.solvo.web.ui.foundation.hasWidth
import org.solvo.web.ui.foundation.ifThenElse
import org.solvo.web.ui.foundation.rememberMutableDebouncedState
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import kotlin.time.Duration.Companion.seconds

@Stable
private val ANSWER_CONTENT_MAX_HEIGHT = 260.dp

@Composable
fun ExpandedAnswer(
    model: QuestionPageViewModel,
    item: CommentDownstream,
) {
    val viewModel = remember(item) { FullCommentCardViewModel(item) }

    val hasWidthToShowThoughtKind = hasWidth(660.dp)
    val isThought by remember { derivedStateOf { item.kind == CommentKind.THOUGHT } }
    AnswerCard(
        author = item.author,
        date = {
            val postTimeFormatted by viewModel.postTimeFormatted.collectAsState("")
            AnswerCardDate(postTimeFormatted) {
                if (isThought && hasWidthToShowThoughtKind) {
                    ThoughtTip()
                }
            }
        },
        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth().wrapContentHeight(),
        subComments = {
            AnswerCardReactions(item, model.controlBarState, model.events)
            Spacer(Modifier.height(6.dp))
        },
        actions = {
            AnswerCardActions(item, model)
        },
    ) { backgroundColor ->
        if (isThought && !hasWidthToShowThoughtKind) {
            ThoughtTip(Modifier.padding(bottom = 12.dp))
        }
        var hasOverflow by remember { mutableStateOf(false) }
        CommentCardContent(
            item = item,
            backgroundColor = backgroundColor,
            modifier = Modifier.wrapContentHeight(),
            showScrollbar = hasOverflow,
            showFullAnswer = null,
            onLayout = { hasOverflow = hasVisualOverflow }
        )  // in column card
    }
}

// when not expanded
@Composable
fun AllAnswersList(
    model: QuestionPageViewModel,
    modifier: Modifier = Modifier,
    onExpandAnswer: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)? = null,
) {
    val onExpandAnswerUpdated by rememberUpdatedState(onExpandAnswer)
    val allItems by model.allAnswers.collectAsState()

    Column(modifier) {
        for (itemLoading in allItems) {
            val item by itemLoading.asFlow().collectAsState(Loading.CommentDownstream)
            if (item === Loading.CommentDownstream) continue

            val viewModel = remember(item) { FullCommentCardViewModel(item) }

            var richTextHasVisualOverflow by rememberMutableDebouncedState(false, 0.1.seconds)
            var isShowingFullAnswer by remember { mutableStateOf(false) } // in list mode
            val singleItem by model.isSingleAnswer.collectAsState()
            val actualShowingFullAnswer = isShowingFullAnswer || singleItem // in list mode

            val hasWidthToShowThoughtKind = hasWidth(660.dp)
            val isThought by remember { derivedStateOf { item.kind == CommentKind.THOUGHT } }
            AnswerCard(
                author = item.author,
                date = {
                    val postTimeFormatted by viewModel.postTimeFormatted.collectAsState("")
                    AnswerCardDate(postTimeFormatted) {
                        if (isThought && hasWidthToShowThoughtKind) {
                            ThoughtTip()
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth().wrapContentHeight(),
                subComments = {
                    AnswerCardPreviewComments(item, onExpandAnswerUpdated)
                    AnswerCardReactions(item, model.controlBarState, model.events)
                    AddYourCommentTextField(onSend = {
                        model.submitComment(CommentUpstream(it), item.coid)
                    })
                    Spacer(Modifier.height(6.dp))
                },
                actions = {
                    AnswerCardActions(item, model)
                },
            ) { backgroundColor ->
                if (isThought && !hasWidthToShowThoughtKind) {
                    ThoughtTip(Modifier.padding(bottom = 12.dp))
                }

                CommentCardContent(
                    item = item,
                    backgroundColor = backgroundColor,
                    modifier = Modifier.ifThenElse(
                        actualShowingFullAnswer,
                        then = { wrapContentHeight() },
                        `else` = { heightIn(max = ANSWER_CONTENT_MAX_HEIGHT) }
                    ),
                    showScrollbar = false,
                    showFullAnswer = {
                        if (!richTextHasVisualOverflow && !actualShowingFullAnswer) {
                            return@CommentCardContent // text is short, no need to display "see more" 
                        }
                        if (singleItem) return@CommentCardContent // single item, always expand

                        Row {
                            Text(
                                if (actualShowingFullAnswer) "see less" else "...see more",
                                Modifier.clickable(indication = null, onClick = wrapClearFocus {
                                    isShowingFullAnswer = !isShowingFullAnswer
                                }).cursorHoverIcon(CursorIcon.POINTER).fillMaxWidth(),
                                fontWeight = FontWeight.W600,
                                fontSize = AuthorNameTextStyle.fontSize,
                                textAlign = TextAlign.End,
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Icon(Icons.Outlined.OpenInNew, "See Full Answer")
                        }
                    }
                ) {
                    richTextHasVisualOverflow = hasVisualOverflow
                } // in column card
            }
        }
    }
}

@Composable
private fun AnswerCardActions(
    item: CommentDownstream,
    model: QuestionPageViewModel
) {
    if (item.isSelf) {
        val snackbar by rememberUpdatedState(LocalTopSnackBar.current)
        HorizontalExpandMenu {
            CommentCardActionButton(Icons.Filled.Edit, "Edit", false) {
                closeMenu()
                model.startEditingAnswer(item)
            }
            CommentCardActionButton(Icons.Filled.Delete, "Delete", true) {
                closeMenu()
                model.askDeleteAnswer(item, snackbar)
            }
        }
    }
}

