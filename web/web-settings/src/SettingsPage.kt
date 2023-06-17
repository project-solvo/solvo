package org.solvo.web.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun SettingsPage(
    pageTitle: @Composable (() -> Unit)? = null,
    navigationRail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ProvideTextStyle(MaterialTheme.typography.headlineLarge) {
        pageTitle?.invoke()
    }

    if (pageTitle != null) {
        Spacer(Modifier.height(40.dp))
    }

    Row {
        Box(Modifier.fillMaxHeight()) {
            navigationRail()
        }

        Column(modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun <T : SettingGroup<*>> SettingsNavigationRail(
    entries: Collection<T>,
    selected: T?,
    icon: @Composable (T) -> Unit = { it.NavigationIcon() },
    label: @Composable (T) -> Unit = { Text(it.pathName) },
    minWidth: Dp = 100.dp,
    onClick: (T) -> Unit,
) {
    BasicSettingsNavigationRail(entries, selected, icon, label, minWidth, onClick)
}

@Composable
fun <T> BasicSettingsNavigationRail(
    entries: Collection<T>,
    selected: T?,
    icon: @Composable (T) -> Unit,
    label: @Composable (T) -> Unit,
    minWidth: Dp = 100.dp,
    onClick: (T) -> Unit,
) {
    NavigationRail(Modifier.fillMaxHeight()) {
        for (entry in entries) {
            val onClickUpdated by rememberUpdatedState(onClick)
            NavigationRailItem(
                selected = selected == entry,
                icon = {
                    icon(entry)
                },
                onClick = { onClickUpdated(entry) },
                modifier = Modifier.widthIn(min = minWidth),
                label = { label(entry) },
                alwaysShowLabel = true,
            )
        }
    }
}
