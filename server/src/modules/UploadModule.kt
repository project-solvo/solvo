package org.solvo.server.modules

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.Answer
import org.solvo.model.Article
import org.solvo.model.api.UploadImageResponse
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose

fun Application.uploadModule() {
    authentication { authBearer() }

    routing {
        authenticate("authBearer") {
            route("/upload") {
                put("/article") {
                    val uid = getUserId() ?: return@put
                    val article = call.receive<Article>()

                    if (uploadNewContentConcerningAnonymity(article, uid, ServerContext.articles)) {
                        call.respond(article)
                    }
                }

                put("/answer") {
                    val uid = getUserId() ?: return@put
                    val answer = call.receive<Answer>()

                    if (uploadNewContentConcerningAnonymity(answer, uid, ServerContext.answers)) {
                        call.respond(answer)
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