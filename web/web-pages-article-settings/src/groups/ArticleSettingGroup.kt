package org.solvo.web.pages.article.settings.groups

import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.settings.SettingGroup

sealed class ArticleSettingGroup(
    pathName: String,
    displayName: String
) : SettingGroup<PageViewModel>(pathName, displayName)