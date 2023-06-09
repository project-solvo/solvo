package org.solvo.server.utils.eventHandler

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.solvo.model.api.events.CommentEvent

interface CommentEventHandler : EventHandler<CommentEvent>

class CommentEventHandlerImpl : CommentEventHandler {
    private val _events = MutableSharedFlow<CommentEvent>()
    override val events = _events.asSharedFlow()

    override suspend fun announce(event: CommentEvent) {
        _events.emit(event)
    }
}