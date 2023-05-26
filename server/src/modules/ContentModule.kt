package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.solvo.server.ServerContext
import java.io.File
import java.util.*

fun Application.contentModule() {
    val db = ServerContext.Databases

    routing {
        get("/images/{resourceId}") {
            val resourceIdStr = call.parameters["resourceId"]!!

            val resourceId = UUID.fromString(resourceIdStr)
            val purpose = ServerContext.Databases.resources.getPurpose(resourceId)
            if (purpose == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val path = ServerContext.paths.staticResourcePath(resourceId, purpose)
            call.respondFile(File(path))
        }
        route("/courses") {
            get {
                call.respond(db.courses.all())
            }
            get("/{courseCode}") {
                val courseCode = call.parameters["courseCode"]!!
                val courseId = db.courses.getId(courseCode)
                if (courseId == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(db.articles.getExistingTermsOfCourse(courseId))
            }
            get("/{courseCode}/{termName}") {
                val courseCode = call.parameters["courseCode"]!!
                val termName = call.parameters["termName"]!!

                val courseId = db.courses.getId(courseCode)
                val termId = db.terms.getId(termName)
                if (courseId == null || termId == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(db.articles.viewAll(courseId, termId))
            }
            get("/{courseCode}/{termName}/{articleName}") {
                val articleId = getArticleIdFromContext() ?: return@get
                call.respond(db.articles.view(articleId)!!)
            }
            get("/{courseCode}/{termName}/{articleName}/{questionIndex}") {
                val articleId = getArticleIdFromContext() ?: return@get

                val questionIndex = call.parameters["questionIndex"]!!
                val questionId = db.questions.getId(articleId, questionIndex)
                if (questionId == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(db.questions.view(questionId)!!)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getArticleIdFromContext(): UUID? {
    val courseCode = call.parameters["courseCode"]!!
    val termName = call.parameters["termName"]!!
    val articleName = call.parameters["articleName"]!!

    val articleId = ServerContext.Databases.articles.getId(courseCode, termName, articleName)
    if (articleId == null) {
        call.respond(HttpStatusCode.NotFound)
    }
    return articleId
}