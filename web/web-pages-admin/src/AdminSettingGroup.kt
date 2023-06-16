package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.solvo.web.groups.OperatorsGroupContent
import org.solvo.web.groups.SystemContent

@Immutable
enum class AdminSettingGroup(
    val displayName: String,
    val icon: ImageVector,
    val content: SettingGroupContent,
) {
    OPERATORS("Operators", Icons.Outlined.AdminPanelSettings, OperatorsGroupContent),
    SYSTEM("System", Icons.Outlined.Settings, SystemContent),
}

@Stable
val AdminSettingGroup.pathName: String get() = this.name.lowercase()


@Stable
interface SettingGroupContent {
    val settingGroup: AdminSettingGroup

    @Composable
    fun ColumnScope.Header(pageViewModel: AdminSettingsPageViewModel) {
        Text(settingGroup.displayName, style = MaterialTheme.typography.headlineMedium)
        Divider(
            Modifier.padding(vertical = 16.dp).fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.5f)
        )
    }

    @Composable
    fun ColumnScope.PageContent(pageViewModel: AdminSettingsPageViewModel)
}

@Composable
fun GroupSurface(
    header: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(modifier.padding(vertical = 16.dp).clip(shape), shape = shape, tonalElevation = 1.dp) {
        Column {
            Surface(Modifier.height(64.dp).fillMaxWidth(), tonalElevation = 8.dp) {
                ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                    Row(
                        Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        header()
                    }
                }
            }

            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CenteredTipText(
    text: @Composable RowScope.() -> Unit,
) {
    Box(Modifier.padding(vertical = 36.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
        ProvideTextStyle(MaterialTheme.typography.titleLarge) {
            Row {
                text()
            }
        }
    }
}

@Composable
fun CenteredTipText(
    text: String,
) = CenteredTipText { Text(text) }
