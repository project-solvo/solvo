package org.solvo.web.ui.foundation

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties

@Composable
inline fun Modifier.ifThen(condition: Boolean, action: Modifier.() -> Modifier): Modifier {
    return if (condition) action()
    else this
}


@Stable
val NO_FOCUS_MODIFIER = Modifier.focusProperties {
    this.canFocus = false
}.focusable(false) // compose bug
