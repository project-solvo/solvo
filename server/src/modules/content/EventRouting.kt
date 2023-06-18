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
import org.solvo.model.api.events.ArticleSettingPageEvent
import org.solvo.model.api.events.CoursePageEvent
import org.solvo.model.api.events.QuestionPageEvent
import org.solvo.server.ServerContext
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.utils.LogManagerKt
import org.solvo.server.utils.events.EventSessionHandler
import org.solvo.server.utils.events.UserSession
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

private object EventRouting

private val logger = LogManagerKt.logger<EventRouting>()


fun Route.eventRouting(
    contents: ContentDBFacade,
    events: EventSessionHandler,
) {
    authenticate("authBearer", optional = true) {
        webSocket("/courses/{courseCode}/events") {
            val path = call.request.path()
            val uid = connectAndGetUid(path)

            val courseCode = call.parameters.getOrFail("courseCode")
            if (contents.getCourseName(courseCode) == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Course does not exist"))
                logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
                return@webSocket
            }

            handleUserSession(events, uid, path) { session ->
                session.events
                    .filter { it is CoursePageEvent && it.courseCode == courseCode }
                    .collect { event ->
                        sendSerialized(event)
                        logger.info { "Sent CoursePageEvent $event" }
                    }
            }
        }
        webSocket("/courses/{courseCode}/articles/{articleCode}/events") {
            val path = call.request.path()
            val uid = connectAndGetUid(path)

            val articleId = contents.getArticleId(
                courseCode = call.parameters.getOrFail("courseCode"),
                code = call.parameters.getOrFail("articleCode")
            ) ?: run {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Article does not exist"))
                logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
                return@webSocket
            }

            handleUserSession(events, uid, path) { session ->
                session.events
                    .filter { it is ArticleSettingPageEvent && it.articleCoid == articleId }
                    .collect { event ->
                        sendSerialized(event)
                        logger.info { "Sent ArticleSettingPageEvent $event" }
                    }
            }
        }
        webSocket("/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}/events") {
            val path = call.request.path()
            val uid = connectAndGetUid(path)

            val questionId = contents.getArticleId(
                courseCode = call.parameters.getOrFail("courseCode"),
                code = call.parameters.getOrFail("articleCode")
            )?.let { articleId ->
                contents.getQuestionId(
                    articleId = articleId,
                    code = call.parameters.getOrFail("questionCode")
                )
            } ?: run {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Question does not exist"))
                logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
                return@webSocket
            }

            handleUserSession(events, uid, path) { session ->
                session.events
                    .filter { it is QuestionPageEvent && it.questionCoid == questionId }
                    .collect { event ->
                        sendSerialized(event)
                        logger.info { "Sent QuestionPageEvent $event" }
                    }
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.connectAndGetUid(path: String): UUID? {
    val token = call.parameters.getOrFail("token")
    val uid = ServerContext.tokens.matchToken(token)
    // val uid = call.principal<UserIdPrincipal>()?.name?.let { UUID.fromString(it) }
    logger.info { "Connection on $path established" + (uid?.let { " with user $it" } ?: "") }
    return uid
}

private suspend inline fun DefaultWebSocketServerSession.handleUserSession(
    events: EventSessionHandler,
    uid: UUID?,
    path: String,
    action: DefaultWebSocketServerSession.(session: UserSession) -> Unit,
) {
    val session = events.register(uid)
    try {
        while (isActive) {
            action(session)
        }
    } catch (e: Throwable) {
        if (e is CancellationException || e is ClosedReceiveChannelException) {
            logger.info { "Connection on $path closed with reason ${closeReason.await()}" }
            return
        }

        logger.info { "Connection on $path closed erroneously with reason ${closeReason.await()}" }
        e.printStackTrace()
    } finally {
        events.destroy(session)
    }
}
