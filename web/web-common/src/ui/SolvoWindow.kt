@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.solvo.web.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeWindow
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.skiko.SkikoView
import org.solvo.web.document.Cookies
import org.solvo.web.session.LocalUserViewModel
import org.solvo.web.session.UserViewModel
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.ui.theme.AppTheme
import org.w3c.dom.Window

@Suppress("FunctionName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun SolvoWindow(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = { }
) {
    ComposeWindow().apply {
        val windowState = createWindowState()

        setContent {
            val isInDarkMode by windowState.preferDarkMode.collectAsState()
            AppTheme(useDarkTheme = isInDarkMode ?: isBrowserInDarkTheme()) {
                val currentSize by windowState.size.collectAsState()
                val snackbarHostState = remember { SolvoSnackbar(SnackbarHostState()) }
                CompositionLocalProvider(
                    LocalSolvoWindow provides windowState,
                    LocalTopSnackBar provides snackbarHostState,
                    LocalContentColor provides MaterialTheme.colorScheme.contentColorFor(
                        MaterialTheme.colorScheme.background
                    ),
                    LocalUserViewModel provides remember { UserViewModel() },
                ) {
                    Box(modifier.size(currentSize).background(MaterialTheme.colorScheme.background)) {
                        Column(Modifier.fillMaxSize()) {
                            content()
                        }
                        SnackbarHost(
                            snackbarHostState.snackbarHostState,
                            Modifier.padding(top = 8.dp).align(Alignment.TopCenter)
                        ) { data ->
                            val theme = snackbarHostState.currentSnackbarTheme
                            if (theme == null) {
                                Snackbar(data)
                            } else {
                                Snackbar(
                                    data,
                                    actionOnNewLine = theme.actionOnNewLine,
                                    shape = theme.shape ?: SnackbarDefaults.shape,
                                    containerColor = theme.containerColor.takeOrElse { SnackbarDefaults.color },
                                    contentColor = theme.contentColor.takeOrElse { SnackbarDefaults.contentColor },
                                    actionColor = theme.actionColor.takeOrElse { SnackbarDefaults.actionColor },
                                    actionContentColor = theme.actionContentColor.takeOrElse { SnackbarDefaults.actionContentColor },
                                    dismissActionContentColor = theme.dismissActionContentColor.takeOrElse { SnackbarDefaults.dismissActionContentColor },
                                )
                            }
                        }
                    }
                }
            }

            val density = LocalDensity.current
            LaunchedEffect(density) {
                console.log("Window size: ${windowState.size.value}")
                console.log("Density: ${density.density}")
                console.log("fontScale: ${density.fontScale}")
            }
        }
    }
}

internal fun ComposeWindow.createWindowState(): WindowState {
    val state = WindowState(this)

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
class WindowState internal constructor(
    private val composeWindow: ComposeWindow,
) {
    init {
        current = this
    }

    val skiaLayer get() = composeWindow.layer.layer
    val skikoView: SkikoView? get() = composeWindow.layer.layer.skikoView
    val canvas get() = composeWindow.canvas

    val size: MutableStateFlow<DpSize> = MutableStateFlow(window.innerSize)
    val preferDarkMode: MutableStateFlow<Boolean?> =
        MutableStateFlow(Cookies.getCookie("is-in-dark-mode")?.toBooleanStrictOrNull())

    val density: MutableStateFlow<Density> = MutableStateFlow(window.currentDensity())

    fun setDarkMode(dark: Boolean?) {
        preferDarkMode.value = dark
        Cookies.setCookie("is-in-dark-mode", dark.toString())
    }

    companion object {
        lateinit var current: WindowState
    }
}

@Composable
fun WindowState.isInDarkMode(): Boolean {
    val preferDark by preferDarkMode.collectAsState()
    return preferDark ?: isBrowserInDarkTheme()
}

/**
 * Workaround for `isSystemInDarkTheme()` which does not work.
 */
@Composable
fun isBrowserInDarkTheme(): Boolean {
    val isInDarkTheme by remember {
        val state = mutableStateOf(window.isInDarkTheme)
        window.matchMedia(MATCH_PREFERS_COLOR_SCHEME).apply {
            addEventListener("change", {
                state.value = window.isInDarkTheme
            })
        }
        state
    }
    return isInDarkTheme
}

private const val MATCH_PREFERS_COLOR_SCHEME = "(prefers-color-scheme: dark)"

val Window.isInDarkTheme: Boolean
    get() {
        return window.matchMedia(MATCH_PREFERS_COLOR_SCHEME).matches
    }

fun Window.currentDensity() = Density(devicePixelRatio.toFloat(), 1f)


val LocalSolvoWindow: ProvidableCompositionLocal<WindowState> = staticCompositionLocalOf { error("Not avialable") }