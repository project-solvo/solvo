package org.solvo.web.comments.commentCard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.web.ui.foundation.wrapClearFocus

val CONTROL_BUTTON_FONT_SIZE = 16.sp

@Composable
fun CommentCardActionButton(
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
