package org.solvo.web.comments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class InteractionBarViewModel(
    private val emojiCountList: List<Int> = listOf()
) {
    val listCounter: MutableState<MutableList<Int>> =
        mutableStateOf(mutableListOf<Int>().apply { addAll(emojiCountList) })

    fun changeEmojiListState() {
        emojiListIsOpen.value = !emojiListIsOpen.value
    }

    val emojiListIsOpen = mutableStateOf(false)

}