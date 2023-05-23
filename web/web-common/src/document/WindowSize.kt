@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.solvo.web.document

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeWindow
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.web.ui.theme.AppTheme
import org.w3c.dom.Window

@Suppress("FunctionName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun SolvoWindow(
    content: @Composable WindowState.() -> Unit = { }
) {
    ComposeWindow().apply {
        val windowState = createWindowState()

        setContent {
            AppTheme(useDarkTheme = true) {
                val currentSize by windowState.size.collectAsState()
                Box(Modifier.size(currentSize)) {
                    content(windowState)
                }
            }

        }
    }
}

internal fun createWindowState(): WindowState {
    val state = WindowState()
    window.onresize = {
//        val canvas = canvas
//        canvas.asDynamic().style.width = window.innerWidth.toString() + "px"
//        canvas.asDynamic().style.height = window.innerHeight.toString() + "px"
        state.size.value = window.innerSize
        true.asDynamic()
    }
    return state
}

val Window.innerSize get() = DpSize(window.innerWidth.dp, window.innerHeight.dp)

@Stable
class WindowState(
    val size: MutableStateFlow<DpSize> = MutableStateFlow(window.innerSize)
)