package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.model.CommentDownstream
import org.solvo.model.LightCommentDownstream
import org.solvo.web.comments.commentCard.ExpandedCommentCard
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.FullCommentCardViewModel
import org.solvo.web.comments.reactions.ReactionBar
import org.solvo.web.comments.subComments.SubComments
import org.solvo.web.ui.foundation.OutlinedTextField
import org.solvo.web.ui.foundation.ifThen
import org.solvo.web.ui.modifiers.clickable

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
                showExpandButton = richTextHasVisualOverflow,
                modifier = Modifier.then(sizeModifier),
                onClickExpand = {
                    onSwitchExpandState?.invoke(index, item)
                },
                isExpand = isExpanded,
                subComments = if (isExpanded) {
                    null
                } else {
                    {
                        SubComments(
                            item.previewSubComments,
                            item.allSubCommentIds.size
                        ) {
                            onClickCommentState?.invoke(it, item)
                        }
                    }
                },
                actions = {},
                reactions = {
                    ReactionBar(item.coid, viewModel.reactions, applyLocalReactionsChange = {
                        viewModel.setReactions(it)
                    })

                    if (!isExpanded) {
                        AddYourCommentTextField(onClickCommentState, item)
                    }
                }
            ) { backgroundColor ->
                CommentCardContent(
                    item,
                    backgroundColor,
                    Modifier.ifThen(!isExpanded) { heightIn(max = ANSWER_CONTENT_MAX_HEIGHT) },
                    showScrollbar = isExpanded && richTextHasVisualOverflow,
                    onLayout = {
                        richTextHasVisualOverflow = hasVisualOverflow
                    },
                ) // in column card
            }
        }
    }
}

@Composable
private fun AddYourCommentTextField(
    onClickComment: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)?,
    item: CommentDownstream
) {
    val onClickCommentState by rememberUpdatedState(onClickComment)
    val itemState by rememberUpdatedState(item)

    OutlinedTextField(
        "", {},
        Modifier
            .height(48.dp)
            .clickable(indication = null) { onClickCommentState?.invoke(null, itemState) }
            .padding(top = 6.dp, bottom = 6.dp)
            .padding(horizontal = 12.dp)
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
