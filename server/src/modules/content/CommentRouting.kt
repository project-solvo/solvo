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
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.api.events.UpdateCommentEvent
import org.solvo.model.api.events.UpdateReactionEvent
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.*
import org.solvo.server.utils.eventHandler.QuestionPageEventHandler
import java.util.*

fun Route.commentRouting(contents: ContentDBFacade, questionPageEvents: QuestionPageEventHandler) {
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
            processUploadComment(contents, asAnswer = false, questionPageEvents)
        }
        postAuthenticated("{parentId}/answer") {
            processUploadComment(contents, asAnswer = true, questionPageEvents)
        }
        postAuthenticated("{coid}/reactions/new") {
            val uid = getUserId() ?: return@postAuthenticated
            val coid: UUID = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = call.receive<ReactionKind>()

            respondOKOrBadRequest(contents.postReaction(coid, uid, kind)) {
                announceUpdateReaction(questionPageEvents, contents, coid, uid, kind)
            }
        }
        deleteAuthenticated("{coid}/reactions/{kind}") {
            val uid = getUserId() ?: return@deleteAuthenticated
            val coid = UUID.fromString(call.parameters.getOrFail("coid"))
            val kind = ReactionKind.entries.getOrNull(
                call.parameters.getOrFail("kind").toIntOrNull() ?: throw BadRequestException("kind must be int")
            ) ?: throw BadRequestException("invalid kind")

            respondOKOrBadRequest(contents.deleteReaction(coid, uid, kind)) {
                announceUpdateReaction(questionPageEvents, contents, coid, uid, kind)
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
    questionPageEvents: QuestionPageEventHandler,
) {
    val uid = getUserId() ?: return
    val parentId = UUID.fromString(call.parameters.getOrFail("parentId"))
    val comment = call.receive<CommentUpstream>()

    val commentId = if (asAnswer) {
        contents.postAnswer(comment, uid, parentId)
    } else {
        contents.postComment(comment, uid, parentId)
    }
    if (commentId != null) {
        val commentDownstream = contents.viewComment(commentId)!!
        val questionId = if (asAnswer) {
            parentId
        } else {
            getQuestionIdOfAnswer(contents, parentId)
        }
        questionPageEvents.announce(UpdateCommentEvent(commentDownstream, questionId))
    }
    respondContentOrBadRequest(commentId)
}

private suspend fun getQuestionIdOfAnswer(
    contents: ContentDBFacade,
    answerId: UUID,
): UUID {
    return contents.viewComment(answerId)?.parent ?: error("111111")
}

private suspend fun announceUpdateReaction(
    questionPageEvents: QuestionPageEventHandler,
    contents: ContentDBFacade,
    coid: UUID,
    uid: UUID,
    kind: ReactionKind
) {
    questionPageEvents.announce(
        UpdateReactionEvent(
            reaction = contents.viewReaction(coid, uid, kind),
            parentCoid = coid,
            questionCoid = getQuestionIdOfAnswer(contents, coid),
        )
    )
}
