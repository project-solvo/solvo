package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.*
import org.solvo.model.api.communication.AuthRequest
import org.solvo.model.api.communication.AuthResponse
import org.solvo.model.api.communication.AuthStatus
import org.solvo.model.api.communication.UsernameValidityResponse
import org.solvo.server.ServerContext


@KtorDsl
inline fun Application.routeApi(crossinline block: Route.() -> Unit) {
    routing {
        route("api") {
            block()
        }
    }
}


val AuthDigest = getDigestFunction("SHA-256") { "ktor$it" }

fun Application.authenticateModule() {
    val accounts = ServerContext.Databases.accounts

    authentication { authBearer() }

    routeApi {
        post("/register") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = AuthDigest(request.password.str)

            val response = accounts.register(username.str, hash)
            call.respondAuth(response)
        }

        get("/register/{username}") {
            val username = call.parameters.getOrFail("username")

            val validity = accounts.getUsernameValidity(username)
            call.respond(UsernameValidityResponse(validity))
        }

        post("/login") {
            val request = call.receive<AuthRequest>()
            val username = request.username
            val hash = AuthDigest(request.password.str)

            val response = accounts.login(username.str, hash)
            call.respondAuth(response)
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

fun AuthenticationConfig.authBearer() {
    bearer("authBearer") {
        authenticate { tokenCredential ->
            ServerContext.tokens.matchToken(tokenCredential.token)?.let { UserIdPrincipal(it.toString()) }
        }
    }
}