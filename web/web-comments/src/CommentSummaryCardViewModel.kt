package org.solvo.web.comments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp

class CommentCardState {
    val showingMore = mutableStateOf(false)
    val authorNameSize = mutableStateOf(25.dp)
    val text = mutableStateOf("See More")

    // TODO: should be a field of commentDownstream
    val date: MutableState<String> = mutableStateOf("16:03, 23/May/2023")

    fun switchSeeMore() {
        showingMore.value = !showingMore.value
        if (!showingMore.value) {
            text.value = "See More"
        } else {
            text.value = "Show Less"
        }
    }

    fun changeDate(newDate: String) {
        date.value = newDate
    }
}