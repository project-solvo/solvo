package org.solvo.web.comments

import androidx.compose.runtime.mutableStateOf

class ShowMoreButtonState {
    val showingMore = mutableStateOf(false)
    val text = mutableStateOf("See More")

    fun switchSeeMore() {
        showingMore.value = !showingMore.value
        if (!showingMore.value) {
            text.value = "See More"
        } else {
            text.value = "Show Less"
        }
    }
}