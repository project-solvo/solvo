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
import org.solvo.server.database.ContentDBFacade
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
            postAuthenticated("/upload") {
                val uid = getUserId() ?: return@postAuthenticated

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
            postAuthenticated("/new") {
                val uid = getUserId() ?: return@postAuthenticated
                if (!accounts.isOp(uid)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@postAuthenticated
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
                postAuthenticated("/upload") {
                    val uid = getUserId() ?: return@postAuthenticated
                    val courseCode = call.parameters.getOrFail("courseCode")
                    val article = call.receive<ArticleUpstream>()

                    val articleId = contents.postArticle(article, uid, courseCode)
                    if (articleId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        call.respond(articleId)
                    }
                }
                get("/{articleCode}") {
                    val articleId = getArticleIdFromContext() ?: return@get
                    call.respond(contents.viewArticle(articleId)!!)
                }
                get("/{articleCode}/questions/{questionCode}") {
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
            get("/get/{coid}") {
                processGetComment(contents, viewFull = false)
            }
            get("/get/{coid}/full") {
                processGetComment(contents, viewFull = true)
            }
            postAuthenticated("/post/{parentId}") {
                processUploadComment(contents, asAnswer = false)
            }
            postAuthenticated("/post/{parentId}/asAnswer") {
                processUploadComment(contents, asAnswer = true)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processGetComment(
    contents: ContentDBFacade,
    viewFull: Boolean,
) {
    val coid = UUID.fromString(call.parameters.getOrFail("coid"))
    val content = if (viewFull) contents.viewFullComment(coid) else contents.viewComment(coid)
    if (content == null) {
        call.respond(HttpStatusCode.NotFound)
    } else {
        call.respond(content)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.processUploadComment(
    contents: ContentDBFacade,
    asAnswer: Boolean,
) {
    val uid = getUserId() ?: return
    val parentId = UUID.fromString(call.parameters.getOrFail("parentId"))
    val comment = call.receive<CommentUpstream>()

    val commentId = if (asAnswer) {
        contents.postAnswer(comment, uid, parentId)
    } else {
        contents.postComment(comment, uid, parentId)
    }
    if (commentId == null) {
        call.respond(HttpStatusCode.BadRequest)
    } else {
        call.respond(commentId)
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