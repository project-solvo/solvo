package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.*
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
                respondContentOrBadRequest(courseId)
            }
            get("/{courseCode}") {
                val courseCode = call.parameters.getOrFail("courseCode")
                val courseName = contents.getCourseName(courseCode)
                respondContentOrNotFound(courseName)
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

        route("/shared-content") {
            postAuthenticated("/upload") {
                val uid = getUserId() ?: return@postAuthenticated
                val content = call.receive<SharedContent>()

                val id = contents.postSharedContent(content)
                respondContentOrBadRequest(id)
            }
        }

        route("/comment") {
            get("/get/{coid}") {
                processGetComment(contents)
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
) {
    val coid = UUID.fromString(call.parameters.getOrFail("coid"))
    val content = contents.viewComment(coid)
    respondContentOrNotFound(content)
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
    respondContentOrBadRequest(commentId)
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