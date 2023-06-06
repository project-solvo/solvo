package org.solvo.web.comments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class InteractionBarViewModel(
    // private val emojiList: List<>
    private val emojiCountList: List<Int> = listOf()
) {
    val listCounter: MutableState<MutableList<Int>> =
        mutableStateOf(mutableListOf<Int>().apply { addAll(emojiCountList) })

    fun changeEmojiListState() {
        emojiListIsOpen.value = !emojiListIsOpen.value
    }

    fun modifyEmojiCount(index: Int) {
        if (index < emojiCountList.size) {
            if (listCounter.value[index] != emojiCountList[index]) {
                listCounter.value[index] = emojiCountList[index]
            } else {
                listCounter.value[index] = listCounter.value[index] + 1
            }
        }
    }

    val emojiListIsOpen = mutableStateOf(false)

}