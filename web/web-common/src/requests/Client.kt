package org.solvo.web.requests

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.browser.window
import kotlinx.serialization.json.Json

val client = Client()

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
    val courses: CourseRequests by lazy { CourseRequests(this) }
    val articles: ArticleRequests by lazy { ArticleRequests(this) }
    val questions: QuestionRequests by lazy { QuestionRequests(this) }
}

suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    if (!this.status.isSuccess()) return null
    return call.bodyNullable(typeInfo<T>()) as T
}
