package org.solvo.server.modules.content

import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import org.solvo.model.api.events.Event
import org.solvo.model.api.events.UpdateReactionServerEvent
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.utils.LogManagerKt
import org.solvo.server.utils.eventHandler.QuestionPageEventHandler
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

private object EventRouting

private val logger = LogManagerKt.logger<EventRouting>()


fun Route.eventRouting(
    contents: ContentDBFacade,
    questionPageEvents: QuestionPageEventHandler,
) {
    authenticate("authBearer", optional = true) {
        webSocket("/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}/events") {
            val path = call.request.path()
            val uid = call.principal<UserIdPrincipal>()?.name?.let { UUID.fromString(it) }
            logger.info { "Connection on $path established" + uid?.let { "with user $it" } }

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
                while (isActive) {
                    questionPageEvents.events.filter { it.questionCoid == questionId }.collect { event ->
                        if (event is UpdateReactionServerEvent) {
                            val clientEvent = event.of(uid)
                            sendSerialized(clientEvent as Event)
                        } else {
                            sendSerialized(event as Event)
                        }
                        logger.info { "Sent QuestionPageEvent $event" }
                    }
                }
            } catch (e: Throwable) {
                if (e is CancellationException || e is ClosedReceiveChannelException) {
                    logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
                    return@webSocket
                }

                logger.info { "Connection on $path closed erroneously with reason ${closeReason.await()}" }
                e.printStackTrace()
            }
        }
    }
}