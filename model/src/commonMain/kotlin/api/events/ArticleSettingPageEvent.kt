package org.solvo.model.api.events

import org.solvo.model.foundation.Uuid

sealed interface ArticleSettingPageEvent : Event {
    val articleCoid: Uuid
}