package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.util.*


suspend fun PipelineContext<Unit, ApplicationCall>.getUserId(): UUID? {
    val uidStr = call.principal<UserIdPrincipal>()?.name
    if (uidStr == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }
    return UUID.fromString(uidStr)
}

suspend fun PipelineContext<Unit, ApplicationCall>.matchUserId(matchUidStr: String?): UUID? {
    val uidStr = call.principal<UserIdPrincipal>()?.name
    if (uidStr == null || matchUidStr != uidStr) {
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }
    return UUID.fromString(uidStr)
}

@KtorDsl
fun Route.putAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer") { put(path, body) }
