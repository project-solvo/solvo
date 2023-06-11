package org.solvo.server.utils.events

import kotlinx.coroutines.flow.MutableSharedFlow
import org.solvo.model.api.events.Event
import java.util.*

open class UserSession(
    val userId: UUID?,
) {
    val events = MutableSharedFlow<Event>()
}
