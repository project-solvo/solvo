package org.solvo.model.api.events

import kotlinx.serialization.Serializable

@Serializable
sealed interface Event

@Serializable
sealed interface ClusteredEvent : Event {
    val dispatchedEvents: List<Event>
}
