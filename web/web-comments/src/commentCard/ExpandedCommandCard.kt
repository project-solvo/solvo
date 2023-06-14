package org.solvo.web.comments.commentCard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.User
import org.solvo.web.comments.commentCard.components.AuthorLine
import org.solvo.web.comments.commentCard.impl.CommentCard
import org.solvo.web.comments.commentCard.impl.CommentCardShape
import org.solvo.web.comments.commentCard.impl.commentCardBackgroundColor
import org.solvo.web.ui.image.RoundedUserAvatar

@Composable
fun ExpandedCommentCard(
    author: User?,
    date: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    shape: Shape = CommentCardShape,
    backgroundColor: Color = commentCardBackgroundColor(),
    subComments: @Composable (ColumnScope.() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    preContent: @Composable (ColumnScope.(backgroundColor: Color) -> Unit) = {},
    content: @Composable (ColumnScope.(backgroundColor: Color) -> Unit),
) {
    CommentCard(
        paddings = CommentCardPaddings.Large,
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        authorLine = {
            AuthorLine(
                icon = {
                    RoundedUserAvatar(author?.avatarUrl, 40.dp)
                },
                authorName = {
                    Text(author?.username?.str ?: "Anonymous")
                },
                date = date,
            ) {
                actions?.invoke()

//                if (isExpand || showExpandButton) {
//                    ExpandButton(
//                        onClickExpand,
//                        isExpand,
//                    )
//                }
            }
        },
        showMoreSwitch = null,
        subComments = subComments,
        contentModifier = contentModifier,
        preContent = preContent,
        content = content,
    )
}


//@Composable
//private fun ExpandButton(
//    onClickExpand: () -> Unit,
//    isExpanded: Boolean,
//    modifier: Modifier = Modifier,
//) {
//    val text = if (!isExpanded) {
//        "Show Full Answer"
//    } else {
//        "Go Back"
//    }
//    FilledTonalButton(
//        onClick = wrapClearFocus { onClickExpand() },
//        modifier,
//        shape = RoundedCornerShape(8.dp),
//        contentPadding = PaddingValues(
//            start = 12.dp,
//            top = 3.dp,
//            end = 12.dp,
//            bottom = 3.dp
//        ),
//    ) {
//        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
//            Text(
//                text,
//                Modifier.padding(start = 4.dp),
//                style = MaterialTheme.typography.titleMedium,
//            )
//        }
//    }
//}


@Composable
fun ModifyMenu(
    contents: @Composable RowScope.() -> Unit,
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    Row {
        AnimatedVisibility(
            showDropDownMenu,
            // enter = slideInHorizontally { it },
            // exit = slideOutHorizontally { it }
        ) {
            Row {
                contents()
            }
        }
        IconButton(
            onClick = { showDropDownMenu = !showDropDownMenu },
        ) {
            Icon(Icons.Filled.MoreVert, "Expand Drop Down Menu")
        }
    }
}