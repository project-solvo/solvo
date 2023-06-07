package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import org.solvo.model.CommentDownstream
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
    onClickExpand: () -> Unit = {},
    isExpand: Boolean = false,
    subComments: @Composable (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    reactions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val state: ShowMoreSwitchState = remember { ShowMoreSwitchState() }
    val buttonShown = remember { false }
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

                ExpandButton(
                    onClickExpand,
                    isExpand,
                )
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
    isExpand: Boolean = false,
    modifier: Modifier = Modifier,
) {
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
        if (!isExpand) {
            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Show Full Answer",
                    modifier = Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        } else {
            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    "Go Back To the Answer List",
                    Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}