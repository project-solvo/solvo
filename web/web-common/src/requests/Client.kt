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
    internal val scope = CoroutineScope(CoroutineExceptionHandler.byWindowAlert())
    val origin = window.location.origin
    val http = HttpClient(Js) {
        install(HttpTimeout) {
            val timeout = 30_000L
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(ContentNegotiation) {
            json(DefaultCommonJson)
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(DefaultCommonJson)
        }
    }
    val token: String? get() = LocalSessionToken.value


    fun isLoginIn() = (token != null)

    fun invalidateToken() {
        LocalSessionToken.remove()
        History.refresh()
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
