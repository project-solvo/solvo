@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.solvo.web.document

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
            val isInDarkMode by windowState.isInDarkMode.collectAsState()
            AppTheme(useDarkTheme = isInDarkMode ?: isSystemInDarkTheme()) {
                val currentSize by windowState.size.collectAsState()
                Column(Modifier.size(currentSize).background(MaterialTheme.colorScheme.background)) {
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
class WindowState {
    val size: MutableStateFlow<DpSize> = MutableStateFlow(window.innerSize)
    val isInDarkMode: MutableStateFlow<Boolean?> =
        MutableStateFlow(Cookies.getCookie("is-in-dark-mode")?.toBooleanStrictOrNull())

    fun setDarkMode(dark: Boolean?) {
        isInDarkMode.value = dark
        Cookies.setCookie("is-in-dark-mode", dark.toString())
    }
}