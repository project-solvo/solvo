package org.solvo.server.modules

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.RegisterReqeust
import org.solvo.model.RegisterResponse

fun Application.accountsModule() {
    routing {
        post("/accounts/register") {
            call.receive<RegisterReqeust>()

            call.respond(
                RegisterResponse(
                
                )
            )
        }
    }
}
