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

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondContentOrBadRequest(
    content: T?
) {
    if (content == null) {
        call.respond(HttpStatusCode.BadRequest)
    } else {
        call.respond(content)
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondContentOrNotFound(
    content: T?
) {
    if (content == null) {
        call.respond(HttpStatusCode.NotFound)
    } else {
        call.respond(content)
    }
}

@KtorDsl
fun Route.postAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer") { post(path, body) }

@KtorDsl
fun Route.deleteAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer") { delete(path, body) }
