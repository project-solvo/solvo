package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.solvo.server.database.AuthTableFacadeImpl
import org.solvo.server.database.DatabaseFactory

fun Application.loginModule() {
    DatabaseFactory.init()
    val authTable = AuthTableFacadeImpl().apply { runBlocking {
        // initialization here
    } }

    val digestFunction = getDigestFunction("SHA-256") { "ktor$it" }

    authentication {
        form("authFormHashed") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val id = authTable.getId(credentials.name) ?: return@validate null
                authTable.matchHash(id, digestFunction(credentials.password)).let {
                    if (it) UserIdPrincipal("user$id") else null
                }
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