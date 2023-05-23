package org.solvo.web.document

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import org.w3c.dom.Window

@Composable
fun rememberWindowState(): WindowState {
    return remember {
        val state = WindowState()
        window.onresize = {
            println("Resize")
            state.size.value = window.innerSize
            true.asDynamic()
        }
        state
    }
}

val Window.innerSize get() = DpSize(window.innerWidth.dp, window.innerHeight.dp)

@Stable
class WindowState(
    val size: MutableStateFlow<DpSize> = MutableStateFlow(window.innerSize)
)