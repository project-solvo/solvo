package org.solvo.web.comments.showMore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import org.solvo.web.ui.modifiers.clickable

@Composable
fun ShowMoreSwitch(state: ShowMoreSwitchState) {
    Text(
        text = state.text.value,
        modifier = Modifier.clickable { state.switchSeeMore() },
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
}

class ShowMoreSwitchState {
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