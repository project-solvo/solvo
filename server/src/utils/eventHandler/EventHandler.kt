package org.solvo.server.utils.eventHandler

import kotlinx.coroutines.flow.SharedFlow

interface EventHandler<T> {
    suspend fun announce(event: T)
    val events: SharedFlow<T>
}