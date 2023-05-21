package org.solvo.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.slf4j.event.Level

object ServerMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = 80
        println("Loopback: http://localhost:$port/")

        val server = embeddedServer(Netty, port = port, module = Application::solvoModule)
        server.start(wait = true)
    }
}

fun Application.solvoModule() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }


    routing {
        get("/") {
            call.respond("Hello!")
        }
    }
}

private val DefaultJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

private suspend fun ApplicationCall.respondJsonElement(
    element: JsonElement,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    respondText(DefaultJson.encodeToString(element), ContentType.Application.Json, status)
}

private suspend fun <T> ApplicationCall.respondJson(
    serializer: KSerializer<T>,
    element: T,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    respondText(DefaultJson.encodeToString(serializer, element), ContentType.Application.Json, status)
}