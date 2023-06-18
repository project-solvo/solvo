package org.solvo.web.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.solvo.web.document.parameters.PathVariable
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.ui.theme.UNICODE_FONT

/**
 * @param VM view model
 */
@Stable
abstract class SettingGroup<in VM : Any>(
    override val pathName: String,
) : PathVariable {
    private var exitConfirmation: (() -> Boolean)? = null
    protected fun registerExitConfirmation(
        block: (() -> Boolean)?
    ) {
        exitConfirmation = block
    }

    fun requestExit(): Boolean = exitConfirmation?.invoke() ?: true

    @Composable
    abstract fun NavigationIcon()

    @Composable
    abstract fun ColumnScope.PageContent(viewModel: VM)
}

@Composable
fun SimpleHeader(
    displayName: String,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Box(Modifier.fillMaxWidth()) {
        Text(
            displayName,
            Modifier.align(Alignment.CenterStart),
            style = MaterialTheme.typography.headlineMedium
        )
        Row(Modifier.align(Alignment.CenterEnd)) {
            actions()
        }
    }
    Divider(
        Modifier.padding(vertical = 16.dp).fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(0.5f)
    )
}

@Composable
fun HeaderWithActions(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Box(Modifier.fillMaxWidth()) {
        Text(title, Modifier.align(Alignment.CenterStart))
        Row(Modifier.align(Alignment.CenterEnd)) {
            actions()
        }
    }
}

@Composable
fun HintText(text: String) {
    Text(
        text,
        style = TextStyle(
            color = LocalContentColor.current.copy(0.8f),
            fontFamily = UNICODE_FONT,
        )
    )
}

@Composable
fun SaveChangesButton(
    text: @Composable () -> Unit = {
        Text("Save Changes")
    },
    onClick: (SolvoSnackbar) -> Unit,
) {
    val snackbar by rememberUpdatedState(LocalTopSnackBar.current)
    val onClickUpdated by rememberUpdatedState(onClick)
    Button(wrapClearFocus { onClickUpdated(snackbar) }) {
        text()
    }
}

