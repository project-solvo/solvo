package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.model.User
import org.solvo.web.comments.commentCard.components.AuthorLine
import org.solvo.web.comments.commentCard.impl.CommentCard
import org.solvo.web.comments.showMore.ShowMoreSwitchState
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.image.RoundedUserAvatar

@Composable
fun ExpandedCommentCard(
    author: User?,
    date: String,
    showExpandButton: Boolean,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    onClickExpand: () -> Unit = {},
    isExpand: Boolean = false,
    subComments: @Composable (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    reactions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val state: ShowMoreSwitchState = remember { ShowMoreSwitchState() }
    CommentCard(
        paddings = CommentCardPaddings.Large,
        state = state,
        modifier = modifier,
        authorLine = {
            AuthorLine(
                icon = {
                    RoundedUserAvatar(author?.avatarUrl, 48.dp)
                },
                authorName = {
                    Text(author?.username ?: "Anonymous")
                },
                date = {
                    Text(date)
                },
            ) {
                actions?.invoke()

                if (isExpand || showExpandButton) {
                    ExpandButton(
                        onClickExpand,
                        isExpand,
                    )
                }
            }
        },
        showMoreSwitch = null,
        subComments = subComments,
        contentModifier = contentModifier,
        reactions = reactions,
        content = content,
    )
}


@Composable
private fun ExpandButton(
    onClickExpand: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val text = if (isExpanded) {
        "Go Back"
    } else {
        "Show Full Answer"
    }
    FilledTonalButton(
        onClick = wrapClearFocus { onClickExpand() },
        modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 3.dp,
            end = 12.dp,
            bottom = 3.dp
        ),
    ) {
        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(
                text,
                Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}