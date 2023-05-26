package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.api.UploadImageResponse
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.util.*

fun Application.accountModule() {
    authentication { authBearer() }

    routeApi {
        authenticate("authBearer") {
            route("/account/{uid}") {
                put("/newAvatar") {
                    val uid = matchUserId(call.parameters["uid"]) ?: return@put
                    val input = call.receiveStream()

                    deleteOldAvatar(uid)
                    val path = uploadNewImage(uid, input, StaticResourcePurpose.USER_AVATAR)

                    call.respond(UploadImageResponse(path))
                }
                get("/avatar") {
                    val uidStr = call.parameters["uid"]
                    val resourceId = ServerContext.Databases.accounts.getAvatar(UUID.fromString(uidStr))
                    if (resourceId == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    val path = ServerContext.paths.staticResourcePath(resourceId, StaticResourcePurpose.USER_AVATAR)
                    call.respondFile(File(path))
                }
            }
        }
    }
}

private suspend fun deleteOldAvatar(uid: UUID) {
    val oldAvatarId = ServerContext.Databases.accounts.getAvatar(uid)
    if (oldAvatarId != null) {
        val path = ServerContext.paths.staticResourcePath(oldAvatarId, StaticResourcePurpose.USER_AVATAR)
        ServerContext.files.delete(path)
    }
}
