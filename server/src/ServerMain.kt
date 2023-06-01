package org.solvo.server

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.slf4j.event.Level
import org.solvo.server.modules.accountModule
import org.solvo.server.modules.authenticateModule
import org.solvo.server.modules.contentModule
import org.solvo.server.modules.webPageModule

object ServerMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = System.getenv("PORT")?.toIntOrNull() ?: 80
        println("Loopback: http://localhost:$port/")

        val server = embeddedServer(Netty, port = port, module = Application::solvoModules)
        ServerContext.init()
        server.start(wait = true)
    }
}

fun Application.solvoModules() {
    basicModule()
    webPageModule()
    authenticateModule()
    accountModule()
    contentModule()
}

fun Application.basicModule() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
}

private val DefaultJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal suspend fun ApplicationCall.respondJsonElement(
    element: JsonElement,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    respondText(DefaultJson.encodeToString(element), ContentType.Application.Json, status)
}

internal suspend fun <T> ApplicationCall.respondJson(
    serializer: KSerializer<T>,
    element: T,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    respondText(DefaultJson.encodeToString(serializer, element), ContentType.Application.Json, status)
}
