package org.solvo.server.modules.content

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.solvo.model.api.communication.ArticleEditRequest
import org.solvo.model.api.communication.Course
import org.solvo.model.api.communication.QuestionEditRequest
import org.solvo.model.api.events.RemoveArticleEvent
import org.solvo.model.api.events.RemoveQuestionEvent
import org.solvo.model.api.events.UpdateArticleEvent
import org.solvo.model.api.events.UpdateQuestionEvent
import org.solvo.model.utils.NonBlankString
import org.solvo.server.database.AccountDBFacade
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.*
import org.solvo.server.utils.events.EventSessionHandler

fun Route.courseRouting(
    contents: ContentDBFacade,
    accounts: AccountDBFacade,
    events: EventSessionHandler,
) {
    route("/courses") {
        get {
            call.respond(contents.allCourses())
        }
        postAuthenticated("/new") {
            getUserIdAndCheckOp(accounts) ?: return@postAuthenticated
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
            get("/{articleCode}") {
                val articleId = getArticleIdFromContext() ?: return@get
                call.respond(contents.viewArticle(articleId)!!)
            }
            postAuthenticated("/{articleCode}") {
                val uid = getUserIdAndCheckOp(accounts) ?: return@postAuthenticated
                val courseCode = call.parameters.getOrFail("courseCode")
                val articleCode = NonBlankString.fromStringOrNull(
                    call.parameters.getOrFail("articleCode")
                ) ?: throw BadRequestException("empty article code")

                val articleId = contents.createArticle(articleCode, uid, courseCode)
                respondContentOrBadRequest(articleId) {
                    events.announce(UpdateArticleEvent(contents.viewArticle(it)!!))
                }
            }
            delete {
                getUserIdAndCheckOp(accounts) ?: return@delete
                val articleId = getArticleIdFromContext() ?: return@delete
                respondOKOrBadRequest(contents.deleteArticle(articleId)) {
                    events.announce(RemoveArticleEvent(articleId))
                }
            }
            patchAuthenticated("/{articleCode}") {
                val uid = getUserIdAndCheckOp(accounts) ?: return@patchAuthenticated
                val articleId = getArticleIdFromContext() ?: return@patchAuthenticated
                val article = call.receive<ArticleEditRequest>()

                respondOKOrBadRequest(contents.editArticle(article, uid, articleId)) {
                    events.announce(UpdateArticleEvent(contents.viewArticle(articleId)!!))
                }
            }
            route("/{articleCode}/questions/{questionCode}") {
                get {
                    val articleId = getArticleIdFromContext() ?: return@get
                    val questionCode = call.parameters.getOrFail("questionCode")

                    val question = contents.viewQuestion(articleId, questionCode)
                    respondContentOrNotFound(question)
                }
                postAuthenticated("") {
                    val uid = getUserIdAndCheckOp(accounts) ?: return@postAuthenticated
                    val articleId = getArticleIdFromContext() ?: return@postAuthenticated
                    val questionCode = NonBlankString.fromStringOrNull(
                        call.parameters.getOrFail("questionCode")
                    ) ?: throw BadRequestException("empty question code")

                    val questionId = contents.createQuestion(questionCode, articleId, uid)
                    respondContentOrBadRequest(questionId) {
                        events.announce(UpdateQuestionEvent(contents.viewQuestion(it)!!))
                    }
                }
                patchAuthenticated {
                    val uid = getUserIdAndCheckOp(accounts) ?: return@patchAuthenticated
                    val questionId = contents.getQuestionId(
                        articleId = getArticleIdFromContext() ?: return@patchAuthenticated,
                        code = call.parameters.getOrFail("questionCode")
                    ) ?: throw NotFoundException("question not found")
                    val question = call.receive<QuestionEditRequest>()

                    respondOKOrBadRequest(contents.editQuestion(question, uid, questionId)) {
                        events.announce(UpdateQuestionEvent(contents.viewQuestion(questionId)!!))
                    }
                }
                delete {
                    getUserIdAndCheckOp(accounts) ?: return@delete
                    val articleId = getArticleIdFromContext() ?: return@delete
                    val questionId = contents.getQuestionId(
                        articleId = articleId,
                        code = call.parameters.getOrFail("questionCode")
                    ) ?: throw NotFoundException("question not found")

                    respondOKOrBadRequest(contents.deleteQuestion(questionId)) {
                        events.announce(RemoveQuestionEvent(articleId, questionId))
                    }
                }
            }
        }
    }
}
