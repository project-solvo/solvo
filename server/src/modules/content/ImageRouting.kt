package org.solvo.server.modules.content

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.api.communication.ImageUrlExchange
import org.solvo.server.ServerContext
import org.solvo.server.database.ResourceDBFacade
import org.solvo.server.modules.getUserId
import org.solvo.server.modules.postAuthenticated
import org.solvo.server.utils.ServerPathType
import org.solvo.server.utils.StaticResourcePurpose

fun Route.imageRouting(resources: ResourceDBFacade) {
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
}