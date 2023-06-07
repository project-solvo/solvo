package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.api.communication.*
import org.solvo.server.ServerContext
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.utils.ServerPathType
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.util.*

fun Application.contentModule() {
    val contents = ServerContext.Databases.contents
    val accounts = ServerContext.Databases.accounts
    val resources = ServerContext.Databases.resources

    routing {
        staticFiles("/resources", File(ServerContext.paths.resourcesPath()), index = null) {
            // contentType { resources.getContentType(it) }
            preCompressed(CompressedFileType.GZIP, CompressedFileType.BROTLI)
            cacheControl {
                listOf(CacheControl.MaxAge(64000, visibility = CacheControl.Visibility.Public))
            }
        }
    }

    routeApi {
        route("/images") {
            postAuthenticated("/upload") {
                val uid = getUserId() ?: return@postAuthenticated

                val contentType = call.request.contentType()
                val input = call.receiveStream()
                val imageId = resources.postImage(uid, input, StaticResourcePurpose.TEXT_IMAGE, contentType)
                val path = ServerContext.paths.resolveResourcePath(
                    imageId,
                    StaticResourcePurpose.TEXT_IMAGE,
                    ServerPathType.REMOTE
                )

                call.respond(ImageUrlExchange(path))
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
                if (courseName == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(Course(courseCode, courseName))
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
                val content = call.receive<SharedContent>()

                val id = contents.postSharedContent(content)
                respondContentOrBadRequest(id)
            }
        }

        route("/comments") {
            get("{coid}") {
                processGetComment(contents)
            }
            authenticate("authBearer", optional = true) {
                get("{coid}/reactions") {
                    val coid = UUID.fromString(call.parameters.getOrFail("coid"))
                    val userId = call.principal<UserIdPrincipal>()?.name?.let { UUID.fromString(it) }
                    call.respond(contents.viewAllReactions(coid, userId))
                }
            }
            postAuthenticated("{parentId}/comment") {
                processUploadComment(contents, asAnswer = false)
            }
            postAuthenticated("{parentId}/answer") {
                processUploadComment(contents, asAnswer = true)
            }
            postAuthenticated("{coid}/reactions/new") {
                val uid = getUserId() ?: return@postAuthenticated
                val coid = UUID.fromString(call.parameters.getOrFail("coid"))
                val reaction = call.receive<ReactionKind>()
                call.respond(
                    if (contents.postReaction(coid, uid, reaction)) {
                        HttpStatusCode.OK
                    } else {
                        HttpStatusCode.BadRequest
                    }
                )
            }
            deleteAuthenticated("{coid}/reactions/{kind}") {
                val uid = getUserId() ?: return@deleteAuthenticated
                val coid = UUID.fromString(call.parameters.getOrFail("coid"))
                val kind = ReactionKind.entries.getOrNull(
                    call.parameters.getOrFail("kind").toIntOrNull() ?: throw BadRequestException("kind must be int")
                ) ?: throw BadRequestException("invalid kind")

                call.respond(
                    if (contents.deleteReaction(coid, uid, kind)) {
                        HttpStatusCode.OK
                    } else {
                        HttpStatusCode.BadRequest
                    }
                )
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