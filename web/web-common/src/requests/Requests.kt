package org.solvo.web.requests

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.solvo.model.api.events.Event
import kotlin.time.Duration.Companion.seconds

abstract class Requests {
    abstract val client: Client

    protected fun api(url: String): String {
        return "${apiUrl}/${url.removePrefix("/")}"
    }

    protected fun connectEvents(path: String): SharedFlow<Event> {
        val flow = MutableSharedFlow<Event>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        client.scope.launch {
            while (isActive) {
                try {
                    val session = http.webSocketSession {
                        url.takeFrom(path)
                        url.protocol = URLProtocol.WS
                    }
                    while (isActive) {
                        val event = session.receiveDeserialized<Event>()
                        flow.emit(event)
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    delay(5.seconds)
                }
            }
        }
        return flow
    }

    protected companion object {
        fun HttpRequestBuilder.accountAuthorization() {
            val token = client.token ?: return
            headers {
                bearerAuth(token)
            }
        }
    }
}

class NotAuthorizedException() : Exception()


@Suppress("UnusedReceiverParameter")
val Requests.origin get() = window.location.origin.removeSuffix("/")
val Requests.apiUrl get() = "$origin/api"

val Requests.http get() = client.http
