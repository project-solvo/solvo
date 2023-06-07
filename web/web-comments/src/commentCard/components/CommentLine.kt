package org.solvo.web.comments.commentCard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.web.ui.image.RoundedUserAvatar
import org.solvo.web.ui.theme.UNICODE_FONT

@Composable
fun CommentLine(
    subComment: LightCommentDownstream,
    modifier: Modifier = Modifier
) {
    CommentLine(
        modifier,
        icon = {
            RoundedUserAvatar(subComment.author?.avatarUrl, 24.dp)
        },
        authorName = { Text(subComment.author?.username ?: "") }, // TODO: 2023/5/29 handle anonymous 
    ) {
        Text(subComment.content)
    }
}

@Composable
fun CommentLine(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    authorName: (@Composable () -> Unit)? = null,
    message: @Composable RowScope.() -> Unit = {},
) {
    Row(modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(24.dp)) {
            icon?.invoke()
        }

        Spacer(Modifier.width(8.dp))

        if (authorName != null) {
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(AuthorNameTextStyle) {
                    authorName()
                }
            }
            Text(": ", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = UNICODE_FONT)
        }

        ProvideTextStyle(
            LocalTextStyle.current.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                fontFamily = UNICODE_FONT,
            )
        ) {
            message()
        }
    }
}
