package org.solvo.server.modules.content

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.api.communication.*
import org.solvo.model.api.events.UpdateCommentEvent
import org.solvo.model.api.events.UpdateReactionEvent
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.*
import org.solvo.server.utils.events.EventSessionHandler
import java.util.*

fun Route.commentRouting(contents: ContentDBFacade, events: EventSessionHandler) {
    route("/comments") {
        getOptionallyAuthenticated("{coid}") {
            processGetComment(contents)
        }
        patchAuthenticated("{coid}") {
            processEditComment(contents, events)
        }
        deleteAuthenticated("{coid}") {
            processDeleteComment(contents, events)
        }
        getOptionallyAuthenticated("{coid}/reactions") {
            val coid = UUID.fromString(call.parameters.getOrFail("coid"))
            val userId = call.principal<UserIdPrincipal>()?.let { UUID.fromString(it.name) }
            call.respond(contents.viewAllReactions(coid, userId))
        }
        postAuthenticated("{parentId}/comment") {
            processUploadComment(contents, CommentKind.COMMENT, events)
        }
        postAuthenticated("{parentId}/answer") {
            processUploadComment(contents, CommentKind.ANSWER, events)
        }
        postAuthenticated("{parentId}/thought") {
            processUploadComment(contents, CommentKind.THOUGHT, events)
        }
        postAuthenticated("{coid}/reactions/new") {
            val uid = getUserId() ?: return@postAuthenticated
            val coid: UUID = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = call.receive<ReactionKind>()

            respondOKOrBadRequest(contents.postReaction(coid, uid, kind)) {
                events.announceUpdateReaction(contents, coid, kind)
            }
        }
        deleteAuthenticated("{coid}/reactions/{kind}") {
            val uid = getUserId() ?: return@deleteAuthenticated
            val coid = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = ReactionKind.entries.getOrNull(
                call.parameters.getOrFail("kind").toIntOrNull() ?: throw BadRequestException("kind must be int")
            ) ?: throw BadRequestException("invalid kind")

            respondOKOrBadRequest(contents.deleteReaction(coid, uid, kind)) {
                events.announceUpdateReaction(contents, coid, kind)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processGetComment(
    contents: ContentDBFacade,
) {
    val uid = call.principal<UserIdPrincipal>()?.let { UUID.fromString(it.name) }
    val coid = UUID.fromString(call.parameters.getOrFail("coid"))
    val content = contents.viewComment(coid, uid)
    respondContentOrNotFound(content)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processEditComment(
    contents: ContentDBFacade,
    events: EventSessionHandler,
) {
    val uid = getUserId() ?: return
    val coid = UUID.fromString(call.parameters.getOrFail("coid"))
    val request = call.receive<CommentEditRequest>()

    val comment = contents.viewComment(coid) ?: throw NotFoundException("coid not found")

    respondOKOrBadRequest(contents.editComment(request, coid, uid)) {
        announceUpdateComment(coid, comment.kind, comment.parent, contents, events)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processDeleteComment(
    contents: ContentDBFacade,
    events: EventSessionHandler,
) {
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processUploadComment(
    contents: ContentDBFacade,
    kind: CommentKind,
    events: EventSessionHandler,
) {
    val uid = getUserId() ?: return
    val parentId: UUID = UUID.fromString(call.parameters.getOrFail("parentId"))
    val comment = call.receive<CommentUpstream>()

    val commentId = when (kind) {
        CommentKind.COMMENT -> contents.postComment(comment, uid, parentId)
        CommentKind.ANSWER -> contents.postAnswer(comment, uid, parentId)
        CommentKind.THOUGHT -> contents.postThought(comment, uid, parentId)
    }
    announceUpdateComment(commentId, kind, parentId, contents, events)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.announceUpdateComment(
    commentId: UUID?,
    kind: CommentKind,
    parentId: UUID,
    contents: ContentDBFacade,
    events: EventSessionHandler
) {
    if (commentId != null) {
        val questionId: UUID
        if (kind.isAnswerOrThought()) {
            questionId = parentId
        } else {
            questionId = contents.getCommentParentId(parentId)!!
            events.announce {
                UpdateCommentEvent(contents.viewComment(parentId, this.userId)!!, questionId)
            } // also announce its parent (answer)
        }
        events.announce {
            UpdateCommentEvent(contents.viewComment(commentId, this.userId)!!, questionId)
        }
    }
    respondContentOrBadRequest(commentId)
}

private suspend fun EventSessionHandler.announceUpdateReaction(
    contents: ContentDBFacade,
    coid: UUID,
    kind: ReactionKind
) {
    val userIds = contents.viewUsersOfReaction(coid, kind)
    val questionId = contents.viewComment(coid)!!.parent
    announce {
        UpdateReactionEvent(
            reaction = Reaction(
                kind = kind,
                count = userIds.size,
                isSelf = userIds.contains(userId),
            ),
            parentCoid = coid,
            questionCoid = questionId,
        )
    }
}
