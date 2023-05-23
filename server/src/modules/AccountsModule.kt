package org.solvo.server.modules

import io.ktor.http.*
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
import org.solvo.server.TokenGeneratorImpl
import org.solvo.server.database.AuthTableFacadeImpl
import org.solvo.server.database.DatabaseFactory

fun Application.accountModule() {
    DatabaseFactory.init()
    val authTable = AuthTableFacadeImpl().apply {
        runBlocking {
            // initialization here
        }
    }
    val tokenGenerator = TokenGeneratorImpl()
    val digestFunction = getDigestFunction("SHA-256") { "ktor$it" }

    routing {
        post("/register") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = digestFunction(request.password)

            if (authTable.getId(username) != null) {
                call.respondAuth(AuthResponse(AuthStatus.DUPLICATED_USERNAME))
                return@post
            }

            val status = checkUserNameValidity(username)
            if (status == AuthStatus.SUCCESS) {
                authTable.addAuth(username, hash)
            }

            call.respondAuth(AuthResponse(status))
        }
        post("/login") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = digestFunction(request.password)

            val id = authTable.getId(username)
            if (id == null) {
                call.respondAuth(AuthResponse(AuthStatus.USER_NOT_FOUND))
                return@post
            }

            call.respondAuth(
                if (authTable.matchHash(id, hash)) {
                    AuthResponse(AuthStatus.SUCCESS, tokenGenerator.generateToken(id))
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
