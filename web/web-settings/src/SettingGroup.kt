package org.solvo.web.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.web.document.parameters.PathVariable

/**
 * @param VM view model
 */
@Immutable
abstract class SettingGroup<VM : Any>(
    override val pathName: String,
) : PathVariable {
    @Composable
    abstract fun NavigationIcon()

    @Composable
    abstract fun ColumnScope.PageContent(viewModel: VM)

    companion object {
        @Composable
        protected fun SimpleHeader(displayName: String) {
            Text(displayName, style = MaterialTheme.typography.headlineMedium)
            Divider(
                Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(0.5f)
            )
        }
    }
}
