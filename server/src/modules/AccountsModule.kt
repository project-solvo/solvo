package org.solvo.server.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.solvo.model.AccountChecker.checkUserNameValidity
import org.solvo.model.AuthRequest
import org.solvo.model.AuthResponse
import org.solvo.model.AuthStatus
import org.solvo.server.database.AuthTableFacadeImpl
import org.solvo.server.database.DatabaseFactory

fun Application.accountModule() {
    DatabaseFactory.init()
    val authTable = AuthTableFacadeImpl().apply {
        runBlocking {
            // initialization here
        }
    }
    val digestFunction = getDigestFunction("SHA-256") { "ktor$it" }

    configRouting(authTable)
}

private fun Application.configRouting(authTable: AuthTableFacadeImpl) {
    routing {
        post("/register") {
            val request = call.receive<AuthRequest>()

            if (authTable.getId(request.username) != null) {
                call.respond(AuthResponse(AuthStatus.DUPLICATED_USERNAME))
                return@post
            }

            val status = checkUserNameValidity(request.username)
            if (status == AuthStatus.SUCCESS) {
                authTable.addAuth(request.username, request.hash)
            }

            call.respond(AuthResponse(status))
        }
        post("/login") {
            val request = call.receive<AuthRequest>()

            val id = authTable.getId(request.username)
            if (id == null) {
                call.respond(AuthResponse(AuthStatus.USER_NOT_FOUND))
                return@post
            }

            call.respond(
                if (authTable.matchHash(id, request.hash)) {
                    AuthResponse(AuthStatus.SUCCESS)
                    // TODO: create token
                } else {
                    AuthResponse(AuthStatus.WRONG_PASSWORD)
                }
            )
        }
    }
}
