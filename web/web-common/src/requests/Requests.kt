package org.solvo.web.requests

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
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

    protected fun connectEvents(
        scope: CoroutineScope,
        path: String,
    ): SharedFlow<Event> {
        val flow = MutableSharedFlow<Event>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        var session: DefaultClientWebSocketSession? = null
        val job = scope.launch {
            while (isActive) {
                try {
                    session = http.webSocketSession {
                        url.takeFrom(path)
                        url.protocol = URLProtocol.WS
                    }
                    println("Event connected: $path")
                    while (isActive) {
                        val event = session!!.receiveDeserialized<Event>()
                        println("Received event: $event")
                        flow.emit(event)
                    }
                    println("Event disconnected normally")
                } catch (e: Throwable) {
                    println("Event disconnected exceptionally")
                    e.printStackTrace()
                    delay(5.seconds)
                }
            }
        }
        job.invokeOnCompletion {
            client.scope.launch { session?.close() }
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
