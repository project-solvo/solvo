package org.solvo.web.requests

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import org.solvo.model.utils.DefaultCommonJson
import org.solvo.web.document.History
import org.solvo.web.session.LocalSessionToken
import org.solvo.web.utils.byWindowAlert

val client = Client()

class Client {
    val scope = CoroutineScope(CoroutineExceptionHandler.byWindowAlert())
    val origin = window.location.origin
    val json = DefaultCommonJson
    val http = HttpClient(Js) {
        install(HttpTimeout) {
            val timeout = 30_000L
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(json)
        }
    }
    val token: String? get() = LocalSessionToken.value


    fun isLoginIn() = (token != null)
    fun checkLoggedIn() {
        if (!isLoginIn()) {
            History.navigate { auth() }
        }
    }

    fun invalidateToken() {
        LocalSessionToken.remove()
        History.refresh()
    }

    val accounts: AccountRequests by lazy { AccountRequests(this) }
    val courses: CourseRequests by lazy { CourseRequests(this) }
    val articles: ArticleRequests by lazy { ArticleRequests(this) }
    val questions: QuestionRequests by lazy { QuestionRequests(this) }
    val comments: CommentRequests by lazy { CommentRequests(this) }
    val images: ImageRequests by lazy { ImageRequests(this) }
}

suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    if (!this.status.isSuccess()) return null
    return call.bodyNullable(typeInfo<T>()) as T
}
