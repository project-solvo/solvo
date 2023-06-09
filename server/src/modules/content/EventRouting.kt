package org.solvo.server.modules.content

import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.filter
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.utils.eventHandler.CommentEventHandler


fun Route.eventRouting(
    contents: ContentDBFacade,
    commentUpdates: CommentEventHandler,
) {
    webSocket("/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}/events") {
        println("onConnect")

        val courseCode = call.parameters.getOrFail("courseCode")
        val articleCode = call.parameters.getOrFail("articleCode")
        val questionCode = call.parameters.getOrFail("questionCode")

        val questionId = contents.getArticleId(courseCode, articleCode)?.let { articleId ->
            contents.getQuestionId(articleId, questionCode)
        } ?: run {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "QuestionId does not exist"))
            return@webSocket
        }

        try {
            while (true) {
                commentUpdates.events.filter { commentEvent -> commentEvent.parentCoid == questionId }
                    .collect { event ->
                        sendSerialized(event)
                    }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("onClose ${closeReason.await()}")
        } catch (e: Throwable) {
            println("onError ${closeReason.await()}")
            e.printStackTrace()
        }
    }
}