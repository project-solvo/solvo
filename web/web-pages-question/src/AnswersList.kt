package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.web.comments.commentCard.ExpandedCommentCard
import org.solvo.web.comments.commentCard.components.AuthorLineDateTextStyle
import org.solvo.web.comments.commentCard.components.AuthorNameTextStyle
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.FullCommentCardViewModel
import org.solvo.web.comments.reactions.ReactionBar
import org.solvo.web.comments.reactions.ReactionsIconButton
import org.solvo.web.comments.reactions.rememberReactionBarViewModel
import org.solvo.web.comments.subComments.SubComments
import org.solvo.web.ui.foundation.OutlinedTextField
import org.solvo.web.ui.foundation.ifThen
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon

@Stable
private val ANSWER_CONTENT_MAX_HEIGHT = 260.dp

// when not expanded
@Composable
fun AnswersList(
    allItems: List<CommentDownstream>,
    visibleIndices: IntRange,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onSwitchExpand: ((index: Int, item: CommentDownstream) -> Unit)? = null,
    onClickComment: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)? = null,
) {
    val onSwitchExpandState by rememberUpdatedState(onSwitchExpand)
    val onClickCommentState by rememberUpdatedState(onClickComment)

    Column(modifier) {
        val allItemsIndexed = remember(allItems) { allItems.withIndex() }
        for ((index, item) in allItemsIndexed) {
            @Suppress("NAME_SHADOWING")
            val item by rememberUpdatedState(item)

            val viewModel = remember(item) { FullCommentCardViewModel(item) }
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

            var richTextHasVisualOverflow by remember { mutableStateOf(false) }

            val postTimeFormatted by viewModel.postTimeFormatted.collectAsState(null)

            ExpandedCommentCard(
                author = item.author,
                date = postTimeFormatted ?: "",
                showExpandButton = false,
                modifier = Modifier.then(sizeModifier),
                onClickExpand = {
                    onSwitchExpandState?.invoke(index, item)
                },
                isExpand = isExpanded,
                subComments = {
                    if (!isExpanded) {
                        ProvideTextStyle(TextStyle(fontSize = AuthorLineDateTextStyle.fontSize)) {
                            SubComments(
                                item.previewSubComments,
                                item.allSubCommentIds.size,
                                Modifier.padding(top = 4.dp)
                            ) {
                                onClickCommentState?.invoke(it, item)
                            }
                        }
                    }

                    val reactionBarState = rememberReactionBarViewModel(item.coid, viewModel.reactions)

                    ReactionBar(
                        reactionBarState,
                        applyLocalReactionsChange = {
                            viewModel.setReactions(it)
                        },
                        Modifier.heightIn(max = 42.dp),
                    )

                    Row(Modifier.padding(bottom = 6.dp).offset(x = (-12).dp)) {
                        ReactionsIconButton(reactionBarState)
                        if (!isExpanded) {
                            AddYourCommentTextField(
                                onClickCommentState,
                                item,
                            )
                        }
                    }
                },
                actions = {},
            ) { backgroundColor ->
                CommentCardContent(
                    item,
                    backgroundColor,
                    Modifier.ifThen(!isExpanded) { heightIn(max = ANSWER_CONTENT_MAX_HEIGHT) },
                    showScrollbar = isExpanded && richTextHasVisualOverflow,
                    showFullAnswer = {
                        Row {
                            Text(
                                "... see full answer",
                                Modifier.clickable(indication = null, onClick = wrapClearFocus<Unit> {
                                    onClickCommentState?.invoke(null, item)
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
private fun AddYourCommentTextField(
    onClickComment: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)?,
    item: CommentDownstream,
    modifier: Modifier = Modifier,
) {
    val onClickCommentState by rememberUpdatedState(onClickComment)
    val itemState by rememberUpdatedState(item)

    OutlinedTextField(
        "", {},
        modifier
            .height(48.dp)
            .clickable(indication = null) { onClickCommentState?.invoke(null, itemState) }
            .padding(top = 6.dp, bottom = 6.dp) // inner
            .fillMaxWidth(),
        readOnly = true,
        placeholder = {
            Text(
                "Add your comment...",
                Modifier.clickable(indication = null) { onClickCommentState?.invoke(null, itemState) }
                    .fillMaxWidth()
            )
        },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
        )
    )
}
