package org.solvo.web.requests

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

val client = Client()

val backgroundScope = CoroutineScope(CoroutineExceptionHandler { _, throwable ->
    window.alert(throwable.toString())
})

class Client {
    val origin = window.location.origin
    val http = HttpClient(Js) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    val accounts: AccountRequests by lazy { AccountRequests(this) }
}