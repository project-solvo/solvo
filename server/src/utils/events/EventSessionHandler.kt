package org.solvo.server.utils.events

import org.solvo.model.api.events.ClusteredEvent
import org.solvo.model.api.events.Event
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

interface EventSessionHandler {
    fun register(userId: UUID? = null): UserSession
    fun destroy(session: UserSession)
    suspend fun announce(event: Event)
    suspend fun announce(getEvent: suspend UserSession.() -> Event)
}

class EventSessionHandlerImpl : EventSessionHandler {
    private val sessions: ConcurrentLinkedQueue<UserSession> = ConcurrentLinkedQueue()

    override fun register(userId: UUID?): UserSession {
        return UserSession(userId).also { sessions.add(it) }
    }

    override fun destroy(session: UserSession) {
        sessions.remove(session)
    }

    override suspend fun announce(event: Event) {
        for (session in sessions) {
            session.events.emit(event)
            if (event is ClusteredEvent) {
                event.dispatchedEvents.forEach { session.events.emit(it) }
            }
        }
    }

    override suspend fun announce(getEvent: suspend UserSession.() -> Event) {
        for (session in sessions) {
            val event = session.getEvent()
            session.events.emit(event)
            if (event is ClusteredEvent) {
                event.dispatchedEvents.forEach { session.events.emit(it) }
            }
        }
    }
}

