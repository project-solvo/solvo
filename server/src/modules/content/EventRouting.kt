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
import org.solvo.server.utils.eventHandler.CommentEventHandler


fun Route.eventRouting(
    contents: ContentDBFacade,
    commentUpdates: CommentEventHandler,
) {
    webSocket("/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}/events") {
        val path = call.request.path()
        println("Connection on $path established")

        val courseCode = call.parameters.getOrFail("courseCode")
        val articleCode = call.parameters.getOrFail("articleCode")
        val questionCode = call.parameters.getOrFail("questionCode")

        val questionId = contents.getArticleId(courseCode, articleCode)?.let { articleId ->
            contents.getQuestionId(articleId, questionCode)
        } ?: run {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "QuestionId does not exist"))
            println("Connection on $path closed with reason ${closeReason.await()}")
            return@webSocket
        }

        try {
            while (true) {
                commentUpdates.events.filter { commentEvent ->
                    val answerId = commentEvent.parentCoid
                    contents.viewComment(answerId)!!.parent == questionId
                }.collect { event ->
                    sendSerialized(event as Event)
                    println("Sent CommentEvent with coid ${event.commentCoid} and parent ${event.parentCoid}")
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("Connection on $path closed with reason ${closeReason.await()}")
        } catch (e: Throwable) {
            println("Connection on $path closed erroneously with reason ${closeReason.await()}")
            e.printStackTrace()
        }
    }
}