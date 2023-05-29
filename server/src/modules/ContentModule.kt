package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.solvo.server.ServerContext
import java.util.*

fun Application.contentModule() {
    val contents = ServerContext.Databases.contents

    routeApi {
        get("/images/{resourceId}") {
            val resourceId = UUID.fromString(call.parameters["resourceId"]!!)
            val file = contents.getImage(resourceId)
            if (file == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respondFile(file)
            }
        }

        route("/courses") {
            get {
                call.respond(contents.allCourses())
            }
            get("/{courseCode}") {
                val courseCode = call.parameters["courseCode"]!!
                val articles = contents.allArticlesOfCourse(courseCode)
                if (articles == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(articles)
                }
            }
            get("/{courseCode}/articles/{articleName}") {
                val articleId = getArticleIdFromContext() ?: return@get
                call.respond(contents.viewArticle(articleId)!!)
            }
            get("/{courseCode}/articles/{articleName}/questions/{questionIndex}") {
                val articleId = getArticleIdFromContext() ?: return@get
                val questionIndex = call.parameters["questionIndex"]!!

                val question = contents.viewQuestion(articleId, questionIndex)
                if (question == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(question)
                }
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getArticleIdFromContext(): UUID? {
    val courseCode = call.parameters["courseCode"]!!
    val articleName = call.parameters["articleName"]!!

    val articleId = ServerContext.Databases.contents.getArticleId(courseCode, articleName)
    if (articleId == null) {
        call.respond(HttpStatusCode.NotFound)
    }
    return articleId
}