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
import org.solvo.web.document.History
import org.solvo.web.session.LocalSessionToken

val client = Client()

class Client {
    val origin = window.location.origin
    val http = HttpClient(Js) {
        install(HttpTimeout) {
            val timeout = 30_000L
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            socketTimeoutMillis = timeout
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
    val token: String? get() = LocalSessionToken.value


    fun isLoginIn() = (token != null)

    fun logOut() {
        LocalSessionToken.remove()
        refresh()
    }

    fun jumpToLoginPage() {
        History.navigate {
            auth()
        }
    }

    fun refresh() {
        window.location.href = window.location.href
    }

    val accounts: AccountRequests by lazy { AccountRequests(this) }
    val courses: CourseRequests by lazy { CourseRequests(this) }
    val articles: ArticleRequests by lazy { ArticleRequests(this) }
    val questions: QuestionRequests by lazy { QuestionRequests(this) }
    val comments: CommentRequests by lazy { CommentRequests(this) }
}

suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    if (!this.status.isSuccess()) return null
    return call.bodyNullable(typeInfo<T>()) as T
}
