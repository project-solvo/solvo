package org.solvo.web.requests

import io.ktor.client.request.*
import kotlinx.browser.window

abstract class Requests {
    abstract val client: Client

    protected fun api(url: String): String {
        return "${apiUrl}/${url.removePrefix("/")}"
    }

    protected companion object {
        fun HttpRequestBuilder.accountAuthorization() {
            val token = client.token ?: throw NotAuthorizedException()
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
