package org.solvo.server.modules.content

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.api.communication.ArticleUpstream
import org.solvo.model.api.communication.Course
import org.solvo.model.api.communication.QuestionUpstream
import org.solvo.server.ServerContext
import org.solvo.server.database.AccountDBFacade
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.getUserId
import org.solvo.server.modules.postAuthenticated
import org.solvo.server.modules.respondContentOrBadRequest
import org.solvo.server.modules.respondContentOrNotFound
import java.util.*

fun Route.courseRouting(
    contents: ContentDBFacade,
    accounts: AccountDBFacade
) {
    route("/courses") {
        get {
            call.respond(contents.allCourses())
        }
        postAuthenticated("/new") {
            val uid = getUserId() ?: return@postAuthenticated
            if (!accounts.isOp(uid)) {
                call.respond(HttpStatusCode.Forbidden)
                return@postAuthenticated
            }
            val course = call.receive<Course>()

            val courseId = contents.newCourse(course)
            respondContentOrBadRequest(courseId)
        }
        get("/{courseCode}") {
            val courseCode = call.parameters.getOrFail("courseCode")
            val courseName = contents.getCourseName(courseCode)
            if (courseName == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(Course.fromString(courseCode, courseName))
        }
        route("/{courseCode}/articles") {
            get {
                val courseCode = call.parameters.getOrFail("courseCode")
                val articles = contents.allArticlesOfCourse(courseCode)
                respondContentOrNotFound(articles)
            }
            postAuthenticated("/upload") {
                val uid = getUserId() ?: return@postAuthenticated
                val courseCode = call.parameters.getOrFail("courseCode")
                val article = call.receive<ArticleUpstream>()

                val articleId = contents.postArticle(article, uid, courseCode)
                respondContentOrBadRequest(articleId)
            }
            get("/{articleCode}") {
                val articleId = getArticleIdFromContext() ?: return@get
                call.respond(contents.viewArticle(articleId)!!)
            }
            get("/{articleCode}/questions/{questionCode}") {
                val articleId = getArticleIdFromContext() ?: return@get
                val questionCode = call.parameters.getOrFail("questionCode")

                val question = contents.viewQuestion(articleId, questionCode)
                respondContentOrNotFound(question)
            }
            postAuthenticated("/{articleCode}/questions/{questionCode}/upload") {
                val uid = getUserId() ?: return@postAuthenticated
                val articleId = getArticleIdFromContext() ?: return@postAuthenticated
                val questionCode = call.parameters.getOrFail("questionCode")
                val question = call.receive<QuestionUpstream>()

                val questionId = contents.postQuestion(question, uid, articleId, questionCode)
                respondContentOrBadRequest(questionId)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getArticleIdFromContext(): UUID? {
    val courseCode = call.parameters.getOrFail("courseCode")
    val articleCode = call.parameters.getOrFail("articleCode")

    val articleId = ServerContext.Databases.contents.getArticleId(courseCode, articleCode)
    if (articleId == null) {
        call.respond(HttpStatusCode.NotFound)
    }
    return articleId
}