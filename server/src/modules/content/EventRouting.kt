package org.solvo.server.modules.content

import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.filter
import org.solvo.model.api.events.Event
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.utils.LogManagerKt
import org.solvo.server.utils.eventHandler.CommentEventHandler

private object EventRouting

private val logger = LogManagerKt.logger<EventRouting>()


fun Route.eventRouting(
    contents: ContentDBFacade,
    commentUpdates: CommentEventHandler,
) {
    webSocket("/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}/events") {
        val path = call.request.path()
        logger.info { "Connection on $path established" }

        val courseCode = call.parameters.getOrFail("courseCode")
        val articleCode = call.parameters.getOrFail("articleCode")
        val questionCode = call.parameters.getOrFail("questionCode")

        val questionId = contents.getArticleId(courseCode, articleCode)?.let { articleId ->
            contents.getQuestionId(articleId, questionCode)
        } ?: run {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "QuestionId does not exist"))
            logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
            return@webSocket
        }

        try {
            while (true) {
                commentUpdates.events.filter { commentEvent ->
                    val answerId = commentEvent.parentCoid
                    contents.viewComment(answerId)?.parent == questionId // is sub comment
                            || commentEvent.parentCoid == questionId // is answer or comment to the question
                }.collect { event ->
                    sendSerialized(event as Event)
                    println("Sent CommentEvent with coid ${event.commentCoid} and parent ${event.parentCoid}")
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
        } catch (e: Throwable) {
            logger.info { "Connection on $path closed erroneously with reason ${closeReason.await()}" }
            e.printStackTrace()
        }
    }
}