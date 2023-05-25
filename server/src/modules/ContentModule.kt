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
            val resourceIdStr = call.parameters["resourceId"]
            if (resourceIdStr == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val resourceId = UUID.fromString(resourceIdStr)
            val purpose = ServerContext.resources.getPurpose(resourceId)
            if (purpose == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val path = ServerContext.paths.staticResourcePath(resourceId, purpose)
            call.respondFile(File(path))
        }
        route("/courses/{courseCode}") {
            get {

            }
            route("/{termName}") {
                get {

                }

                get("/{articleID}") {

                }

                get("/{articleID}/{questionIndex}") {

                }
            }
        }
    }
}