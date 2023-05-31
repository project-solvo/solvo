package org.solvo.web.comments.column

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.solvo.model.CommentDownstream

class CommentCardState(modifier: Modifier, commentDownstream: CommentDownstream) {
    var seeMore = mutableStateOf(false)
    var authorNameSize = mutableStateOf(25.dp)
    var currentCardModifier = mutableStateOf(modifier)
    private val cardModifier = mutableStateOf(modifier)
    var text = mutableStateOf("See More")
    // TODO: should be a field of commentDownstream
    var date: MutableState<String> = mutableStateOf("16:03, 23/May/2023")

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