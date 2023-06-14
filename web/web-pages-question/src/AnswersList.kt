package org.solvo.web

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.model.api.events.Event
import org.solvo.web.comments.commentCard.ExpandedCommentCard
import org.solvo.web.comments.commentCard.ModifyMenu
import org.solvo.web.comments.commentCard.components.AuthorLineDateTextStyle
import org.solvo.web.comments.commentCard.components.AuthorNameTextStyle
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.FullCommentCardViewModel
import org.solvo.web.comments.reactions.ReactionBar
import org.solvo.web.comments.reactions.ReactionsIconButton
import org.solvo.web.comments.reactions.rememberReactionBarViewModel
import org.solvo.web.comments.subComments.SubComments
import org.solvo.web.dummy.Loading
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.OutlinedTextField
import org.solvo.web.ui.foundation.ifThenElse
import org.solvo.web.ui.foundation.rememberMutableDebouncedState
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import org.solvo.web.ui.snackBar.SnackbarTheme
import org.solvo.web.viewModel.LoadingUuidItem
import kotlin.time.Duration.Companion.seconds

@Stable
private val ANSWER_CONTENT_MAX_HEIGHT = 260.dp

// when not expanded
@Composable
fun AnswersList(
    allItems: List<LoadingUuidItem<CommentDownstream>>,
    visibleIndices: IntRange,
    isExpanded: Boolean,
    events: Flow<Event>,
    backgroundScope: CoroutineScope,
    modifier: Modifier = Modifier,
    onClickComment: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)? = null,
) {
    val onClickCommentState by rememberUpdatedState(onClickComment)

    Column(modifier) {
        val allItemsIndexed = remember(allItems) { allItems.withIndex() }
        for ((index, itemLoading) in allItemsIndexed) {
            val item by itemLoading.asFlow().collectAsState(Loading.CommentDownstream)
            if (item === Loading.CommentDownstream) continue // TODO: replace dummy loading data with some scaffold when data is not ready

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

            var richTextHasVisualOverflow by rememberMutableDebouncedState(false, 0.1.seconds)
            var isShowingFullAnswer by remember { mutableStateOf(false) } // in list mode

            val postTimeFormatted by viewModel.postTimeFormatted.collectAsState(null)

            ExpandedCommentCard(
                author = item.author,
                date = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(postTimeFormatted ?: "")

                        if (item.kind.toDraftKind() == DraftKind.THOUGHT) {
                            BoxWithConstraints {
                                val maxWidth = maxWidth
                                Row(
//                                Modifier.border(1.dp, color = MaterialTheme.colorScheme.outline).fillMaxWidth()
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(DraftKind.THOUGHT.icon, null, Modifier.height(20.dp))
                                    AnimatedVisibility(maxWidth >= 320.dp) {
                                        Text(
                                            "This might not be a complete answer",
                                            Modifier.padding(start = 8.dp),
                                            fontWeight = FontWeight.W400,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.then(sizeModifier),
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

                    val reactionBarState = rememberReactionBarViewModel(item.coid, events)

                    Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                        ReactionsIconButton(reactionBarState, Modifier.offset(x = (-12).dp))

                        ReactionBar(
                            reactionBarState,
                            Modifier.heightIn(max = 42.dp),
                        )
                    }

                    Row(Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
//                        if (isReactionsEmpty) {
//                            ReactionsIconButton(reactionBarState, Modifier.offset(x = (-12).dp))
//                        }
                        if (!isExpanded) {
                            AddYourCommentTextField(
                                { onClickCommentState?.invoke(null, item) },
                            )
                        }
                    }
                },
                actions = {
                    ModifyMenu {
                        val snackbar by rememberUpdatedState(LocalTopSnackBar.current)
                        ModifyButton(Icons.Filled.Edit, "Edit", false) {
                            backgroundScope.launch {
                                // TODO: 2023/6/14 goto edit 
                            }
                        }
                        ModifyButton(Icons.Filled.Delete, "Delete", true) {
                            backgroundScope.launch {
                                val res = snackbar.showSnackbar(
                                    "Are you sure to delete this? Deletion can not be revoked!",
                                    actionLabel = "Delete",
                                    withDismissAction = true,
                                    theme = SnackbarTheme(
                                        actionColor = Color.Red
                                    ),
                                )
                                if (res == SnackbarResult.ActionPerformed) {
                                    client.comments.deleteComment(item.coid)
                                }
                            }
                        }
                    }
                },
            ) { backgroundColor ->
                CommentCardContent(
                    item,
                    backgroundColor,
                    Modifier
                        .ifThenElse(
                            isExpanded || isShowingFullAnswer,
                            then = { wrapContentHeight() },
                            `else` = { heightIn(max = ANSWER_CONTENT_MAX_HEIGHT) }
                        ),
                    showScrollbar = isExpanded && richTextHasVisualOverflow,
                    showFullAnswer = if (isExpanded) null else {
                        showFullAnswer@{
                            if (!richTextHasVisualOverflow && !isShowingFullAnswer) {
                                return@showFullAnswer // text is short, no need to display "see more" 
                            }

                            Row {
                                Text(
                                    if (isShowingFullAnswer) "see less" else "...see more",
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
                    }
                ) {
                    richTextHasVisualOverflow = hasVisualOverflow
                } // in column card
            }
        }
    }
}

@Composable
private fun ModifyButton(
    imageVector: ImageVector,
    text: String,
    isDelete: Boolean,
    onClick: () -> Unit = {},
) {
    val onClickUpdated by rememberUpdatedState(onClick)
    FilledTonalButton(
        onClick = wrapClearFocus(onClickUpdated),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(if (!isDelete) MaterialTheme.colorScheme.secondary else Color.Red.copy(0.7f)),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            Icon(imageVector, text)
        }

        Box(Modifier.padding(start = 2.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            ProvideTextStyle(TextStyle(fontSize = CONTROL_BUTTON_FONT_SIZE)) {
                Text(text)
            }
        }
    }
}

@Composable
private fun AddYourCommentTextField(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    @Suppress("NAME_SHADOWING")
    val onClick by rememberUpdatedState(onClick)

    OutlinedTextField(
        "", {},
        modifier
            .height(48.dp)
            .clickable(indication = null) { onClick?.invoke() }
            .padding(top = 6.dp, bottom = 6.dp) // inner
            .fillMaxWidth(),
        readOnly = true,
        placeholder = {
            Text(
                "Add your comment...",
                Modifier.clickable(indication = null) { onClick?.invoke() }
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
