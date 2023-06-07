package org.solvo.web.comments.reactions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.solvo.web.ui.foundation.NO_FOCUS_MODIFIER

@Suppress("NAME_SHADOWING")
@Composable
fun ThumbActions(
    likeCount: UInt,
    reaction: Boolean?, // true: like, false: dislike, null: no reaction
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit,
) {
    val focusManager by rememberUpdatedState(LocalFocusManager.current)
    val onClickLike by rememberUpdatedState(onClickLike)
    val onClickDislike by rememberUpdatedState(onClickDislike)

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            focusManager.clearFocus()
            onClickLike()
        }) {
            Icon(
                Icons.Filled.ThumbUp, "Like",
                NO_FOCUS_MODIFIER,
                tint = if (reaction == true) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            )
        }
        Text(remember(likeCount) { "$likeCount" }, Modifier.padding(end = 4.dp))
    }

    IconButton(onClick = {
        focusManager.clearFocus()
        onClickDislike()
    }) {
        Icon(
            Icons.Filled.ThumbDown, "Dislike",
            NO_FOCUS_MODIFIER,
            tint = if (reaction == false) MaterialTheme.colorScheme.primary else LocalContentColor.current,
        )
    }
}
