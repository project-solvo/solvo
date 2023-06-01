package org.solvo.web.comments.like

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.web.ui.foundation.NO_FOCUS_MODIFIER

@Composable
fun ThumbActions(
    likeCount: Int,
    reaction: Boolean?, // true: like, false: dislike, null: no reaction
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClickLike, Modifier.indication(remember { MutableInteractionSource() }, null)) {
            Icon(
                Icons.Filled.ThumbUp, "Like",
                NO_FOCUS_MODIFIER,
                tint = if (reaction == true) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            )
        }
        Text(remember(likeCount) { "$likeCount" }, Modifier.padding(end = 4.dp))
    }

    IconButton(onClick = onClickDislike) {
        Icon(
            Icons.Filled.ThumbDown, "Dislike",
            NO_FOCUS_MODIFIER,
            tint = if (reaction == false) MaterialTheme.colorScheme.primary else LocalContentColor.current,
        )
    }
}
