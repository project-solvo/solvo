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
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose

fun Application.uploadModule() {
    val db = ServerContext.Databases

    authentication { authBearer() }

    routing {
        authenticate("authBearer") {
            route("/upload") {
                put("/article") {
                    val uid = getUserId() ?: return@put
                    val author = ServerContext.Databases.accounts.getUserInfo(uid)!!
                    val article = call.receive<ArticleUpstream>()

                    for (question in article.questions) {
                        if (question.index.length > DatabaseModel.QUESTION_INDEX_MAX_LENGTH) {
                            call.respond(HttpStatusCode.BadRequest)
                            return@put
                        }
                    }

                    val articleId = db.articles.post(article, author)
                    if (articleId != null) {
                        assert(article.questions.map { question ->
                            db.questions.post(question, author, articleId) != null
                        }.all { it })
                        call.respond(articleId)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                put("/answer") {
                    val uid = getUserId() ?: return@put
                    val author = ServerContext.Databases.accounts.getUserInfo(uid)!!
                    val answer = call.receive<AnswerUpstream>()

                    val answerId = db.answers.post(answer, author)
                    if (answerId != null) {
                        call.respond(answerId)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                put("/image") {
                    val uid = getUserId() ?: return@put

                    val input = call.receiveStream()
                    val path = uploadNewImage(uid, input, StaticResourcePurpose.TEXT_IMAGE)

                    call.respond(UploadImageResponse(path))
                }
            }
        }
    }
}