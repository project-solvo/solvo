package org.solvo.web.ui.foundation

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalFocusManager


@Stable
val NO_FOCUS_MODIFIER = Modifier.focusProperties {
    this.canFocus = false
}.focusable(false) // compose bug


@Composable
fun <R> wrapClearFocus(block: () -> R): () -> R {
    val blockState by rememberUpdatedState(block)
    val focusManager by rememberUpdatedState(LocalFocusManager.current)
    return {
        focusManager.clearFocus(true)
        blockState()
    }
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
@Composable
fun <T, R> wrapClearFocus(block: (T) -> R): (T) -> R {
    val blockState by rememberUpdatedState(block)
    val focusManager by rememberUpdatedState(LocalFocusManager.current)
    return {
        focusManager.clearFocus(true)
        blockState(it)
    }
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
@Composable
fun <A, B, R> wrapClearFocus(block: (A, B) -> R): (A, B) -> R {
    val blockState by rememberUpdatedState(block)
    val focusManager by rememberUpdatedState(LocalFocusManager.current)
    return { a, b ->
        focusManager.clearFocus(true)
        blockState(a, b)
    }
}
