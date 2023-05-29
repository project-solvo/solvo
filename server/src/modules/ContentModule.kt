package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.ArticleUpstream
import org.solvo.model.CommentUpstream
import org.solvo.model.Course
import org.solvo.model.api.UploadImageResponse
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import java.util.*

fun Application.contentModule() {
    val contents = ServerContext.Databases.contents
    val accounts = ServerContext.Databases.accounts

    routeApi {
        route("/images") {
            get("/{resourceId}") {
                val resourceId = UUID.fromString(call.parameters.getOrFail("resourceId"))
                val file = contents.getImage(resourceId)
                if (file == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respondFile(file)
                }
            }
            putAuthenticated("/upload") {
                val uid = getUserId() ?: return@putAuthenticated

                val input = call.receiveStream()
                val imageId = contents.postImage(uid, input, StaticResourcePurpose.TEXT_IMAGE)
                val path = ServerContext.paths.staticResourcePath(imageId, StaticResourcePurpose.TEXT_IMAGE)

                call.respond(UploadImageResponse(path))
            }
        }

        route("/courses") {
            get {
                call.respond(contents.allCourses())
            }
            putAuthenticated("/new") {
                val uid = getUserId() ?: return@putAuthenticated
                if (!accounts.isOp(uid)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@putAuthenticated
                }
                val course = call.receive<Course>()

                val courseId = contents.newCourse(course)
                if (courseId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(courseId)
                }
            }
            get("/{courseCode}") {
                val courseCode = call.parameters.getOrFail("courseCode")
                val courseName = contents.getCourseName(courseCode)
                if (courseName == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(Course(courseCode, courseName))
                }
            }
            route("/{courseCode}/articles") {
                get {
                    val courseCode = call.parameters.getOrFail("courseCode")
                    val articles = contents.allArticlesOfCourse(courseCode)
                    if (articles == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respond(articles)
                    }
                }
                putAuthenticated("/upload") {
                    val uid = getUserId() ?: return@putAuthenticated
                    val courseCode = call.parameters.getOrFail("courseCode")
                    val article = call.receive<ArticleUpstream>()

                    val articleId = contents.postArticle(article, uid, courseCode)
                    if (articleId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        call.respond(articleId)
                    }
                }
                get("/{articleName}") {
                    val articleId = getArticleIdFromContext() ?: return@get
                    call.respond(contents.viewArticle(articleId)!!)
                }
                get("/{articleName}/questions/{questionCode}") {
                    val articleId = getArticleIdFromContext() ?: return@get
                    val questionCode = call.parameters.getOrFail("questionCode")

                    val question = contents.viewQuestion(articleId, questionCode)
                    if (question == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respond(question)
                    }
                }
            }
        }

        route("/comment") {
            putAuthenticated("/{parentId}") {
                // TODO
            }
            putAuthenticated("/{questionId}/asAnswer") {
                val uid = getUserId() ?: return@putAuthenticated
                val questionId = UUID.fromString(call.parameters.getOrFail("questionId"))
                val answer = call.receive<CommentUpstream>()

                val answerId = contents.postAnswer(answer, uid, questionId)
                if (answerId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(answerId)
                }
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getArticleIdFromContext(): UUID? {
    val courseCode = call.parameters.getOrFail("courseCode")
    val articleName = call.parameters.getOrFail("articleName")

    val articleId = ServerContext.Databases.contents.getArticleId(courseCode, articleName)
    if (articleId == null) {
        call.respond(HttpStatusCode.NotFound)
    }
    return articleId
}