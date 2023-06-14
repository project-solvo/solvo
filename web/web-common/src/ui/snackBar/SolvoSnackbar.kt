package org.solvo.web.ui.snackBar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.solvo.model.annotations.Stable

@Stable
class SolvoSnackbar(
    val snackbarHostState: SnackbarHostState,
) {
    var currentSnackbarTheme by mutableStateOf<SnackbarTheme?>(null)
        private set

    private val mutex = Mutex()

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        theme: SnackbarTheme? = null,
    ) = mutex.withLock {
        currentSnackbarTheme = theme
        val ret = snackbarHostState.showSnackbar(message, actionLabel, withDismissAction, duration)
        currentSnackbarTheme = null
        ret
    }

    suspend fun showSnackbar(
        snackbarVisuals: SnackbarVisuals,
        theme: SnackbarTheme? = null,
    ) = mutex.withLock {
        currentSnackbarTheme = theme
        val ret = snackbarHostState.showSnackbar(snackbarVisuals)
        currentSnackbarTheme = null
        ret
    }
}

//@JsName("createSnackbarTheme")
//@Composable
//fun SnackbarTheme(
//    actionOnNewLine: Boolean = false,
//    shape: Shape = SnackbarDefaults.shape,
//    containerColor: Color = SnackbarDefaults.color,
//    contentColor: Color = SnackbarDefaults.contentColor,
//    actionColor: Color = SnackbarDefaults.actionColor,
//    actionContentColor: Color = SnackbarDefaults.actionContentColor,
//    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
//): SnackbarTheme = SnackbarThemeImpl(
//    actionOnNewLine,
//    shape,
//    containerColor,
//    contentColor,
//    actionColor,
//    actionContentColor,
//    dismissActionContentColor
//)

fun SnackbarTheme(
    actionOnNewLine: Boolean = false,
    shape: Shape? = null,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    actionColor: Color = Color.Unspecified,
    actionContentColor: Color = Color.Unspecified,
    dismissActionContentColor: Color = Color.Unspecified,
): SnackbarTheme = SnackbarThemeImpl(
    actionOnNewLine,
    shape,
    containerColor,
    contentColor,
    actionColor,
    actionContentColor,
    dismissActionContentColor
)

@Stable
interface SnackbarTheme {
    val actionOnNewLine: Boolean
    val shape: Shape?
    val containerColor: Color
    val contentColor: Color
    val actionColor: Color
    val actionContentColor: Color
    val dismissActionContentColor: Color
}

@Stable
internal class SnackbarThemeImpl(
    override val actionOnNewLine: Boolean = false,
    override val shape: Shape?,
    override val containerColor: Color,
    override val contentColor: Color,
    override val actionColor: Color,
    override val actionContentColor: Color,
    override val dismissActionContentColor: Color,
) : SnackbarTheme