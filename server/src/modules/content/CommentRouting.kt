package org.solvo.server.modules.content

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.api.events.UpdateCommentEvent
import org.solvo.model.api.events.UpdateReactionEvent
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.*
import org.solvo.server.utils.events.EventSessionHandler
import java.util.*

fun Route.commentRouting(contents: ContentDBFacade, events: EventSessionHandler) {
    route("/comments") {
        get("{coid}") {
            processGetComment(contents)
        }
        authenticate("authBearer", optional = true) {
            get("{coid}/reactions") {
                val coid = UUID.fromString(call.parameters.getOrFail("coid"))
                val userId = call.principal<UserIdPrincipal>()?.let { UUID.fromString(it.name) }
                call.respond(contents.viewAllReactions(coid, userId))
            }
        }
        postAuthenticated("{parentId}/comment") {
            processUploadComment(contents, asAnswer = false, events)
        }
        postAuthenticated("{parentId}/answer") {
            processUploadComment(contents, asAnswer = true, events)
        }
        postAuthenticated("{coid}/reactions/new") {
            val uid = getUserId() ?: return@postAuthenticated
            val coid: UUID = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = call.receive<ReactionKind>()

            respondOKOrBadRequest(contents.postReaction(coid, uid, kind)) {
                announceUpdateReaction(events, contents, coid, kind)
            }
        }
        deleteAuthenticated("{coid}/reactions/{kind}") {
            val uid = getUserId() ?: return@deleteAuthenticated
            val coid = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = ReactionKind.entries.getOrNull(
                call.parameters.getOrFail("kind").toIntOrNull() ?: throw BadRequestException("kind must be int")
            ) ?: throw BadRequestException("invalid kind")

            respondOKOrBadRequest(contents.deleteReaction(coid, uid, kind)) {
                announceUpdateReaction(events, contents, coid, kind)
            }
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
    events: EventSessionHandler,
) {
    val uid = getUserId() ?: return
    val parentId: UUID = UUID.fromString(call.parameters.getOrFail("parentId"))
    val comment = call.receive<CommentUpstream>()

    val commentId = if (asAnswer) {
        contents.postAnswer(comment, uid, parentId)
    } else {
        contents.postComment(comment, uid, parentId)
    }
    if (commentId != null) {
        val commentDownstream = contents.viewComment(commentId)!!
        val questionId: UUID
        if (asAnswer) {
            questionId = parentId
        } else {
            val parent = contents.viewComment(parentId)!!
            questionId = parent.parent
            events.announce(UpdateCommentEvent(parent, questionId)) // also announce its parent (answer)
        }
        events.announce(UpdateCommentEvent(commentDownstream, questionId))
    }
    respondContentOrBadRequest(commentId)
}

private suspend fun announceUpdateReaction(
    events: EventSessionHandler,
    contents: ContentDBFacade,
    coid: UUID,
    kind: ReactionKind
) {
    val userIds = contents.viewUsersOfReaction(coid, kind)
    val questionId = contents.viewComment(coid)!!.parent
    events.announce {
        UpdateReactionEvent(
            reaction = Reaction(
                kind = kind,
                count = userIds.size,
                self = userIds.contains(userId),
            ),
            parentCoid = coid,
            questionCoid = questionId,
        )
    }
}
