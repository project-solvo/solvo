package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.solvo.server.ServerContext
import org.solvo.server.database.AccountDBFacade
import java.util.*


suspend fun PipelineContext<Unit, ApplicationCall>.getUserId(): UUID? {
    val uidStr = call.principal<UserIdPrincipal>()?.name
    if (uidStr == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }
    return UUID.fromString(uidStr)
}

suspend fun PipelineContext<Unit, ApplicationCall>.getUserIdAndCheckOp(accounts: AccountDBFacade): UUID? {
    val uid = getUserId() ?: return null
    if (!accounts.isOp(uid)) {
        call.respond(HttpStatusCode.Forbidden)
        return null
    }
    return uid
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
    content: T?,
    processIfSucceed: PipelineContext<Unit, ApplicationCall>.() -> Unit = {},
) {
    if (content == null) {
        call.respond(HttpStatusCode.BadRequest)
    } else {
        call.respond(content)
        processIfSucceed()
    }
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.respondOKOrBadRequest(
    success: Boolean,
    processIfSucceed: PipelineContext<Unit, ApplicationCall>.() -> Unit = {},
) {
    if (success) {
        call.respond(HttpStatusCode.OK)
        processIfSucceed()
    } else {
        call.respond(HttpStatusCode.BadRequest)
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondContentOrNotFound(
    content: T?,
    processIfSucceed: PipelineContext<Unit, ApplicationCall>.() -> Unit = {},
) {
    if (content == null) {
        call.respond(HttpStatusCode.NotFound)
    } else {
        call.respond(content)
        processIfSucceed()
    }
}

@KtorDsl
fun Route.getOptionallyAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer", optional = true) { get(path, body) }

@KtorDsl
fun Route.postAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer") { post(path, body) }

@KtorDsl
fun Route.patchAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer") { patch(path, body) }

@KtorDsl
fun Route.deleteAuthenticated(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
): Route = authenticate("authBearer") { delete(path, body) }


suspend fun PipelineContext<Unit, ApplicationCall>.getArticleIdFromContext(): UUID? {
    val courseCode = call.parameters.getOrFail("courseCode")
    val articleCode = call.parameters.getOrFail("articleCode")

    val articleId = ServerContext.Databases.contents.getArticleId(courseCode, articleCode)
    if (articleId == null) {
        call.respond(HttpStatusCode.NotFound)
    }
    return articleId
}
