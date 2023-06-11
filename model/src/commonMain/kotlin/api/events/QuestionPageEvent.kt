package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import org.solvo.model.foundation.Uuid

@Serializable
sealed interface QuestionPageEvent: Event {
    val questionCoid: Uuid
    val parentCoid: Uuid
}