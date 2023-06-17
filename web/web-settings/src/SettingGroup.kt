package org.solvo.web.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.solvo.web.document.parameters.PathVariable
import org.solvo.web.ui.theme.UNICODE_FONT

/**
 * @param VM view model
 */
@Stable
abstract class SettingGroup<VM : Any>(
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

    companion object {
        @Composable
        protected fun SimpleHeader(
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
        protected fun HintText(text: String) {
            Text(
                text,
                style = TextStyle(
                    color = LocalContentColor.current.copy(0.8f),
                    fontFamily = UNICODE_FONT,
                )
            )
        }
    }
}
