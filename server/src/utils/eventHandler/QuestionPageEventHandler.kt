package org.solvo.server.utils.eventHandler

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.solvo.model.api.events.QuestionPageEvent

interface QuestionPageEventHandler : EventHandler<QuestionPageEvent>

class QuestionPageEventHandlerImpl : QuestionPageEventHandler {
    private val _events = MutableSharedFlow<QuestionPageEvent>()
    override val events = _events.asSharedFlow()

    override suspend fun announce(event: QuestionPageEvent) {
        _events.emit(event)
    }
}