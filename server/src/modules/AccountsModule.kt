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
            val hash = digestFunction(request.hash.toString())

            if (authTable.getId(username) != null) {
                call.respond(AuthResponse(AuthStatus.DUPLICATED_USERNAME))
                return@post
            }

            val status = checkUserNameValidity(username)
            if (status == AuthStatus.SUCCESS) {
                authTable.addAuth(username, hash)
            }

            call.respond(AuthResponse(status))
        }
        post("/login") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = digestFunction(request.hash.toString())

            val id = authTable.getId(username)
            if (id == null) {
                call.respond(AuthResponse(AuthStatus.USER_NOT_FOUND))
                return@post
            }

            call.respond(
                if (authTable.matchHash(id, hash)) {
                    AuthResponse(AuthStatus.SUCCESS, tokenGenerator.generateToken(id))
                } else {
                    AuthResponse(AuthStatus.WRONG_PASSWORD)
                }
            )
        }
    }
}
