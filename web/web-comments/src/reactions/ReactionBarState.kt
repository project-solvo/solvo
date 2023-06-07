package org.solvo.web.comments.reactions

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class ReactionBarState(
    // private val emojiList: List<>
    private val emojiCountList: List<Int> = listOf()
) {
    val listCounter: MutableList<Int> = SnapshotStateList<Int>().apply { addAll(emojiCountList) }

    fun changeEmojiListState() {
        emojiListIsOpen.value = !emojiListIsOpen.value
    }

    fun modifyEmojiCount(index: Int) {
        if (index < emojiCountList.size) {
            if (listCounter[index] != emojiCountList[index]) {
                listCounter[index] = emojiCountList[index]
            } else {
                listCounter[index] = emojiCountList[index] + 1
            }
        }
    }

    val emojiListIsOpen = mutableStateOf(false)

}