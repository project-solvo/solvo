package org.solvo.web.groups

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import org.solvo.web.AdminSettingGroup
import org.solvo.web.AdminSettingsPageViewModel
import org.solvo.web.SettingGroupContent

object SystemContent : SettingGroupContent {
    override val settingGroup: AdminSettingGroup
        get() = AdminSettingGroup.SYSTEM

    @Composable
    override fun ColumnScope.PageContent(pageViewModel: AdminSettingsPageViewModel) {
    }
}