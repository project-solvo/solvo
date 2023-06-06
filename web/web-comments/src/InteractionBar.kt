package org.solvo.web.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.model.foundation.Uuid


@Composable
fun InteractionBar(
    state: InteractionBarViewModel,
    modifier: Modifier = Modifier,
) {
    // Image
    Row {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row {
                IconButton(
                    onClick = { state.changeEmojiListState() },
                ) {
                    Icon(Icons.Filled.EmojiEmotions, "Interaction Button")
                }
            }
            if (state.emojiListIsOpen.value) {
                IconsList()
            }
        }
        Column(
            modifier = Modifier.weight(2f)
        ) {
            for (count in state.listCounter.value) {
                if (count > 0) {

                }
            }
        }
    }


}


@Composable
private fun IconsList(
    iconList: List<Uuid> = listOf(),
    enable: Boolean = false,
) {
    Box {
        // More chips with for loop
        AssistChip(
            onClick = {},
            label = {
                Icon(Icons.Filled.ThumbUp, "Excellent")
            },
            modifier = Modifier.padding(4.dp),
            shape = RoundedCornerShape(4.dp)
        )
    }
}