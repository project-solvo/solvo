package org.solvo.server.modules.content

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.api.communication.ReactionKind
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.*
import java.util.*

fun Route.commentRouting(contents: ContentDBFacade) {
    route("/comments") {
        get("{coid}") {
            processGetComment(contents)
        }
        authenticate("authBearer", optional = true) {
            get("{coid}/reactions") {
                val coid = UUID.fromString(call.parameters.getOrFail("coid"))
                val userId = call.principal<UserIdPrincipal>()?.name?.let { UUID.fromString(it) }
                call.respond(contents.viewAllReactions(coid, userId))
            }
        }
        postAuthenticated("{parentId}/comment") {
            processUploadComment(contents, asAnswer = false)
        }
        postAuthenticated("{parentId}/answer") {
            processUploadComment(contents, asAnswer = true)
        }
        postAuthenticated("{coid}/reactions/new") {
            val uid = getUserId() ?: return@postAuthenticated
            val coid = UUID.fromString(call.parameters.getOrFail("coid"))
            val reaction = call.receive<ReactionKind>()
            call.respond(
                if (contents.postReaction(coid, uid, reaction)) {
                    HttpStatusCode.OK
                } else {
                    HttpStatusCode.BadRequest
                }
            )
        }
        deleteAuthenticated("{coid}/reactions/{kind}") {
            val uid = getUserId() ?: return@deleteAuthenticated
            val coid = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = ReactionKind.entries.getOrNull(
                call.parameters.getOrFail("kind").toIntOrNull() ?: throw BadRequestException("kind must be int")
            ) ?: throw BadRequestException("invalid kind")

            call.respond(
                if (contents.deleteReaction(coid, uid, kind)) {
                    HttpStatusCode.OK
                } else {
                    HttpStatusCode.BadRequest
                }
            )
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processGetComment(
    contents: ContentDBFacade,
) {
    val coid = UUID.fromString(call.parameters.getOrFail("coid"))
    val content = contents.viewComment(coid)
    respondContentOrNotFound(content)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processUploadComment(
    contents: ContentDBFacade,
    asAnswer: Boolean,
) {
    val uid = getUserId() ?: return
    val parentId = UUID.fromString(call.parameters.getOrFail("parentId"))
    val comment = call.receive<CommentUpstream>()

    val commentId = if (asAnswer) {
        contents.postAnswer(comment, uid, parentId)
    } else {
        contents.postComment(comment, uid, parentId)
    }
    respondContentOrBadRequest(commentId)
}
