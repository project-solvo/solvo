package org.solvo.model.api.events

import org.solvo.model.foundation.Uuid

sealed interface QuestionPageEvent : Event {
    val questionCoid: Uuid
    val parentCoid: Uuid
}
