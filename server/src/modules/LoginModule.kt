package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.loginModule() {

    val digestFunction = getDigestFunction("SHA-256") { "ktor$it" }

    val hashedUserTable = UserHashedTableAuth(
        table = mapOf(
            "Fengkai Liu" to digestFunction("ATTACKER"),
            "JerryZ" to digestFunction("uwu")
        ),
        digester = digestFunction
    )

    authentication {
        form("authFormHashed") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
            }
        }
    }

    routing {
        authenticate("authFormHashed") {
            post("/login") {
                val userId = call.principal<UserIdPrincipal>()
                call.respondText(if (userId == null) "nah" else "Hello, ${userId.name}!")
            }
        }
    }
}