package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.AnswerUpstream
import org.solvo.model.ArticleUpstream
import org.solvo.model.api.UploadImageResponse
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose

fun Application.uploadModule() {
    val accounts = ServerContext.Databases.accounts
    val contents = ServerContext.Databases.contents

    routeApi {
        authenticate("authBearer") {
            route("/upload") {
                put("/article") {
                    val uid = getUserId() ?: return@put
                    val author = accounts.getUserInfo(uid)!!
                    val article = call.receive<ArticleUpstream>()

                    val articleId = contents.postArticle(article, author)
                    if (articleId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        call.respond(articleId)
                    }
                }

                put("/answer") {
                    val uid = getUserId() ?: return@put
                    val author = accounts.getUserInfo(uid)!!
                    val answer = call.receive<AnswerUpstream>()

                    val answerId = contents.postAnswer(answer, author)
                    if (answerId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        call.respond(answerId)
                    }
                }

                put("/image") {
                    val uid = getUserId() ?: return@put

                    val input = call.receiveStream()
                    val imageId = contents.postImage(uid, input, StaticResourcePurpose.TEXT_IMAGE)
                    val path = ServerContext.paths.staticResourcePath(imageId, StaticResourcePurpose.TEXT_IMAGE)

                    call.respond(UploadImageResponse(path))
                }
            }
        }
    }
}