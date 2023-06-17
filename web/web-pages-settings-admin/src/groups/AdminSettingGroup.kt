package org.solvo.web.pages.admin.groups

import org.solvo.web.pages.admin.AdminSettingsPageViewModel
import org.solvo.web.settings.SettingGroup

sealed class AdminSettingGroup(
    pathName: String,
    val displayName: String
) : SettingGroup<AdminSettingsPageViewModel>(pathName) {
    companion object {
        val entries: List<AdminSettingGroup> = listOf(
            OperatorsSettingGroup,
            SystemSettingGroup
        )
    }
}