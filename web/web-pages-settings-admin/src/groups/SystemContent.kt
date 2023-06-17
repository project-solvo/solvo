package org.solvo.web.pages.admin.groups

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import org.solvo.web.pages.admin.AdminSettingsPageViewModel

data object SystemSettingGroup : AdminSettingGroup("system", "System") {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Outlined.Settings, null)
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: AdminSettingsPageViewModel) {
    }
}