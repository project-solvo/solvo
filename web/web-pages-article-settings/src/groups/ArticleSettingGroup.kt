package org.solvo.web.pages.article.settings.groups

import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.settings.SettingGroup

sealed class ArticleSettingGroup(
    pathName: String,
) : SettingGroup<PageViewModel>(pathName) {
    companion object {
        // must not be empty
        val articleSettingGroups = listOf(
            ArticlePropertiesSettingGroup,
        )

        val managementGroups = listOf(
            AddQuestionSettingGroup,
        )

    }
}