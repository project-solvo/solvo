package org.solvo.server

import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import org.slf4j.event.Level
import org.solvo.model.utils.DefaultCommonJson
import org.solvo.server.modules.accountModule
import org.solvo.server.modules.authenticateModule
import org.solvo.server.modules.content.contentModule
import org.solvo.server.modules.settingsModule
import org.solvo.server.modules.webPageModule
import java.time.Duration

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
    settingsModule()
    contentModule()
}

fun Application.basicModule() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(ContentNegotiation) {
        json(DefaultCommonJson)
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(DefaultCommonJson)
    }
}