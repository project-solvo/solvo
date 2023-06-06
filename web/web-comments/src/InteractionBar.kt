package org.solvo.web.comments

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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
                IconsList(state)
            }
        }
        Column(
            modifier = Modifier.weight(2f)
        ) {
            // add when connect to backend.
//            for (count in state.listCounter.value) {
//                if (count > 0) {
//                    EmojiChips {
//                        Row {
//                            Icon(Icons.Filled.ThumbUp, "Excellent")
//                            Text("$count")
//                        }
//                    }
//                }
//            }
            Row {
                EmojiChips(
                    state,
                    0,
                    { Icon(Icons.Filled.ThumbUp, "Excellent") },
                ) {
                    Row {
                        Text("${state.listCounter.value[0]}")
                    }
                }

                EmojiChips(
                    state,
                    1,
                    { Icon(Icons.Filled.Celebration, "Celebration") }) {
                    Row {
                        Text("${state.listCounter.value[1]}")
                    }
                }
            }
        }
    }


}


@Composable
private fun IconsList(
    state: InteractionBarViewModel,
    // iconList: List<Uuid> = listOf(),
) {
    Row {
        // More chips with for loop
        IconButton(
            onClick = { state.modifyEmojiCount(0) }
        ) {
            Icon(Icons.Filled.ThumbUp, "Excellent")
        }

        IconButton(
            onClick = { state.modifyEmojiCount(1) }
        ) {
            Icon(Icons.Filled.Celebration, "Celebration")
        }
    }
}

@Composable
private fun EmojiChips(
    state: InteractionBarViewModel,
    index: Int,
    leadingIcon: @Composable () -> Unit,
    text: @Composable () -> Unit,
) {
    AssistChip(
        onClick = {
            // state.modifyEmojiCount(index)
        },
        leadingIcon = leadingIcon,
        label = text,
        modifier = Modifier.padding(4.dp),
    )
}

