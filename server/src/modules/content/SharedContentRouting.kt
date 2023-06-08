package org.solvo.server.modules.content

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.solvo.model.api.communication.SharedContent
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.modules.postAuthenticated
import org.solvo.server.modules.respondContentOrBadRequest

fun Route.sharedContentRouting(contents: ContentDBFacade) {
    route("/shared-content") {
        postAuthenticated("/upload") {
            val content = call.receive<SharedContent>()

            val id = contents.postSharedContent(content)
            respondContentOrBadRequest(id)
        }
    }
}