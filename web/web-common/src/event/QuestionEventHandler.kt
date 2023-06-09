package org.solvo.web.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.events.QuestionEvent
import org.solvo.model.api.events.RemoveQuestionEvent
import org.solvo.model.api.events.UpdateQuestionEvent


fun StateFlow<QuestionDownstream?>.withEvents(
    events: Flow<QuestionEvent>,
    deleted: Deleted
): Flow<QuestionDownstream?> {
    val eventMapped = events.map { event ->
        handleEvent(event, deleted)
    }
    return merge(this, eventMapped)
}

private fun StateFlow<QuestionDownstream?>.handleEvent(
    event: QuestionEvent,
    deleted: Deleted,
): QuestionDownstream? {
    when (event) {
        is RemoveQuestionEvent -> {
            val current = value ?: return null
            if (current.coid == event.questionCoid) {
                deleted.setDeleted()
                return null
            }
            return current
        }

        is UpdateQuestionEvent -> {
            val current = value ?: return null
            if (current.coid == event.articleCoid) return event.question
            return current
        }
    }
}
