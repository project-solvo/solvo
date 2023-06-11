package org.solvo.server.utils.events

import org.solvo.model.api.events.Event
import java.util.*

interface EventSessionHandler {
    fun register(userId: UUID? = null): UserSession
    suspend fun announce(event: Event)
    suspend fun announce(getEvent: UserSession.() -> Event)
}

class EventSessionHandlerImpl: EventSessionHandler {
    private val sessions: MutableSet<UserSession> = mutableSetOf()
    override fun register(userId: UUID?): UserSession {
        return UserSession(userId).also { sessions.add(it) }
    }

    override suspend fun announce(event: Event) {
        for (session in sessions) {
            session.events.emit(event)
        }
    }

    override suspend fun announce(getEvent: UserSession.() -> Event) {
        for (session in sessions) {
            session.events.emit(session.getEvent())
        }
    }
}

