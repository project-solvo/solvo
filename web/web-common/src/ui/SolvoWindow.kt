@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.solvo.web.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeWindow
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.web.document.Cookies
import org.solvo.web.ui.theme.AppTheme
import org.w3c.dom.Window

@Suppress("FunctionName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun SolvoWindow(
    content: @Composable ColumnScope.() -> Unit = { }
) {
    ComposeWindow().apply {
        val windowState = createWindowState()

        setContent {
            val isInDarkMode by windowState.preferDarkMode.collectAsState()
            AppTheme(useDarkTheme = isInDarkMode ?: isSystemInDarkTheme()) {
                val currentSize by windowState.size.collectAsState()
                CompositionLocalProvider(
                    LocalSolvoWindow provides windowState,
                ) {
                    Column(Modifier.size(currentSize).background(MaterialTheme.colorScheme.background)) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.contentColorFor(
                                MaterialTheme.colorScheme.background
                            )
                        ) {
                            content()
                        }
                    }
                }
            }

        }
    }
}

internal fun ComposeWindow.createWindowState(): WindowState {
    val state = WindowState()

    window.onresize = {
//        val canvas = canvas
//        canvas.asDynamic().style.width = window.innerWidth.toString() + "px"
//        canvas.asDynamic().style.height = window.innerHeight.toString() + "px"

        state.size.value = window.innerSize

        if (state.density.value.density != window.devicePixelRatio.toFloat()) {
            val newDensity = Density(window.devicePixelRatio.toFloat(), 1f)
            state.density.value = newDensity
            layer.setDensity(newDensity)
        }

        true.asDynamic()
    }
    return state
}

val Window.innerSize get() = DpSize(window.innerWidth.dp, window.innerHeight.dp)

@Stable
class WindowState {
    val size: MutableStateFlow<DpSize> = MutableStateFlow(window.innerSize)
    val preferDarkMode: MutableStateFlow<Boolean?> =
        MutableStateFlow(Cookies.getCookie("is-in-dark-mode")?.toBooleanStrictOrNull())

    val density: MutableStateFlow<Density> = MutableStateFlow(window.currentDensity())

    fun setDarkMode(dark: Boolean?) {
        preferDarkMode.value = dark
        Cookies.setCookie("is-in-dark-mode", dark.toString())
    }

}

@Composable
fun WindowState.isInDarkMode(): Boolean {
    val preferDark by preferDarkMode.collectAsState()
    return preferDark ?: isSystemInDarkTheme()
}

fun Window.currentDensity() = Density(devicePixelRatio.toFloat(), 1f)


val LocalSolvoWindow: ProvidableCompositionLocal<WindowState> = staticCompositionLocalOf { error("Not avialable") }