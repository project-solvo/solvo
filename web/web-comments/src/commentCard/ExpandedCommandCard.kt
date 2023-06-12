package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.User
import org.solvo.web.comments.commentCard.components.AuthorLine
import org.solvo.web.comments.commentCard.impl.CommentCard
import org.solvo.web.comments.commentCard.impl.commentCardBackgroundColor
import org.solvo.web.ui.image.RoundedUserAvatar

@Composable
fun ExpandedCommentCard(
    author: User?,
    date: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    backgroundColor: Color = commentCardBackgroundColor(),
    subComments: @Composable (ColumnScope.() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.(backgroundColor: Color) -> Unit),
) {
    CommentCard(
        paddings = CommentCardPaddings.Large,
        modifier = modifier,
        authorLine = {
            AuthorLine(
                icon = {
                    RoundedUserAvatar(author?.avatarUrl, 48.dp)
                },
                authorName = {
                    Text(author?.username?.str ?: "Anonymous")
                },
                date = {
                    Text(date)
                },
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
        backgroundColor = backgroundColor,
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