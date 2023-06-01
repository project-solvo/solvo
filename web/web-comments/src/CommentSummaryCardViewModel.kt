package org.solvo.web.comments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class CommentCardState(modifier: Modifier) {
    val seeMore = mutableStateOf(false)
    val authorNameSize = mutableStateOf(25.dp)
    val currentCardModifier = mutableStateOf(modifier)
    private val cardModifier = mutableStateOf(modifier)
    val text = mutableStateOf("See More")

    // TODO: should be a field of commentDownstream
    val date: MutableState<String> = mutableStateOf("16:03, 23/May/2023")

    fun switchSeeMore() {
        seeMore.value = !seeMore.value
    }

    fun switchCardModifier() {
        if (currentCardModifier.value == Modifier) {
            currentCardModifier.value = cardModifier.value
        } else {
            currentCardModifier.value = Modifier
        }
    }


    fun changeText() {
        if (!seeMore.value) {
            text.value = "See More"
        } else {
            text.value = "Show Less"}
    }

    fun changeDate(newDate: String) {
        date.value = newDate
    }
}