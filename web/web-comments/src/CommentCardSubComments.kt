package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.solvo.model.LightCommentDownstream
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon

@Composable
fun CommentCardSubComments(
    subComments: List<LightCommentDownstream>,
    totalCommentCount: Int,
    modifier: Modifier = Modifier,
    onClickComment: ((LightCommentDownstream?) -> Unit)? = null,
) {
    val subCommentsState by rememberUpdatedState(subComments)
    val showComments by derivedStateOf { subCommentsState.take(3) }
    if (showComments.isNotEmpty()) {
        Column(
            modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (subComment in showComments) {
                CommentLine(
                    subComment,
                    Modifier.clickable(indication = null) { onClickComment?.invoke(subComment) },
                )
            }

            CommentLine(
                Modifier
                    .cursorHoverIcon(CursorIcon.POINTER)
                    .clickable(indication = null) { onClickComment?.invoke(null) },
                icon = {
                    Image(
                        Icons.Outlined.Chat, "Show More Comments", Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                },
                message = {
                    Text(
                        remember(totalCommentCount) { "See all $totalCommentCount comments" },
                        Modifier.clickable(indication = null) { onClickComment?.invoke(null) },
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                },
            )
        }
    }
}
