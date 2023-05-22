package org.solvo.server.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.AccountChecker.checkUserNameValidity
import org.solvo.model.LoginResponse
import org.solvo.model.Reason
import org.solvo.model.RegisterReqeust
import org.solvo.model.RegisterResponse

fun Application.accountsModule() {
    routing {
        post("/register") {
            val request = call.receive<RegisterReqeust>()
            // to do
            var reason = checkUserNameValidity(request.username);
            checkNoneExistsUsername(request.username);
            call.respond(
                RegisterResponse(true, reason)
            )
        }
        post("/login") {
            val request = call.receive<RegisterReqeust>();
            // to be implemented
            call.respond(
                LoginResponse(true, Reason.INVALID_USERNAME, "")
            )

        }
    }
}

fun checkNoneExistsUsername(username: String) : Reason {
    // TODO: waiting for database
    return Reason.VALID_USERNAME;
}
