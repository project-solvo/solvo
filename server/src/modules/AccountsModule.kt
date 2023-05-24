package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.solvo.model.api.AccountChecker.checkUserNameValidity
import org.solvo.model.api.AuthRequest
import org.solvo.model.api.AuthResponse
import org.solvo.model.api.AuthStatus
import org.solvo.server.ServerContext

fun Application.accountModule() {
    val accountDB = ServerContext.accountDB
    val digestFunction = getDigestFunction("SHA-256") { "ktor$it" }

    routing {
        post("/register") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = digestFunction(request.password)

            if (accountDB.getId(username) != null) {
                call.respondAuth(AuthResponse(AuthStatus.DUPLICATED_USERNAME))
                return@post
            }

            val status = checkUserNameValidity(username)
            if (status == AuthStatus.SUCCESS) {
                accountDB.addAccount(username, hash)
            }

            call.respondAuth(AuthResponse(status))
        }
        post("/login") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = digestFunction(request.password)

            val id = accountDB.getId(username)
            if (id == null) {
                call.respondAuth(AuthResponse(AuthStatus.USER_NOT_FOUND))
                return@post
            }

            call.respondAuth(
                if (accountDB.matchHash(id, hash)) {
                    AuthResponse(AuthStatus.SUCCESS, ServerContext.tokens.generateToken(id))
                } else {
                    AuthResponse(AuthStatus.WRONG_PASSWORD)
                }
            )
        }
    }
}

private suspend fun ApplicationCall.respondAuth(authResponse: AuthResponse) {
    if (authResponse.status == AuthStatus.SUCCESS) {
        respond(authResponse)
    } else {
        respond(HttpStatusCode.BadRequest, authResponse)
    }
}
