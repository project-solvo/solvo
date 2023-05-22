package org.solvo.web.requests

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

val client = Client()

val backgroundScope = CoroutineScope(EmptyCoroutineContext)

class Client {
    val origin = window.location.origin
    val http = HttpClient(Js) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }

    }

    val accounts: AccountRequests by lazy { AccountRequests(this) }
}