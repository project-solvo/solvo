package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
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
                IconButton(onClick = wrapClearFocus(onClickExpand)) {
                    if (isExpand) {
                        Icon(Icons.Filled.CloseFullscreen, null)
                    } else {
                        Icon(Icons.Filled.OpenInFull, null)
                    }
                }
            }
        },
        onClickCard = onClickCard,
        showMoreSwitch = null,
        subComments = subComments,
        contentModifier = contentModifier,
        reactions = reactions,
        content = content,
    )
}
