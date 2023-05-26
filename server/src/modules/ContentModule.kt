package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.server.ServerContext
import java.io.File
import java.util.*

fun Application.contentModule() {
    routing {
        get("/images/{resourceId}") {
            val resourceIdStr = call.parameters["resourceId"]!!

            val resourceId = UUID.fromString(resourceIdStr)
            val purpose = ServerContext.resources.getPurpose(resourceId)
            if (purpose == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val path = ServerContext.paths.staticResourcePath(resourceId, purpose)
            call.respondFile(File(path))
        }
        route("/courses") {
            get {

            }
            get("/{courseCode}") {

            }
            get("/{courseCode}/{termName}") {

            }
            get("/{courseCode}/{termName}/{articleName}") {

            }
            get("/{courseCode}/{termName}/{articleName}/{questionIndex}") {

            }
        }
    }
}