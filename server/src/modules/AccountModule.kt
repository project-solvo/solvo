package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.api.NewAvatarResponse
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.io.InputStream
import java.util.*

fun Application.accountModule() {
    authentication {
        bearer("authBearer") {
            authenticate { tokenCredential ->
                ServerContext.tokens.matchToken(tokenCredential.token)?.let { UserIdPrincipal(it.toString()) }
            }
        }
    }

    routing {
        authenticate("authBearer") {
            route("/account/{uid}") {
                put("/newAvatar") {
                    val uidStr =  call.parameters["uid"]
                    if (call.principal<UserIdPrincipal>()?.name != uidStr) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@put
                    }
                    val input = call.receiveStream()

                    deleteOldAvatar(uidStr)
                    val path = uploadNewAvatar(uidStr, input)

                    call.respond(NewAvatarResponse(path))
                }
                get("/avatar") {
                    val uidStr =  call.parameters["uid"]
                    val resourceId = ServerContext.accounts.getAvatar(UUID.fromString(uidStr))
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

private suspend fun uploadNewAvatar(uidStr: String?, input: InputStream): String {
    val newAvatarId = ServerContext.resources.addResource(StaticResourcePurpose.USER_AVATAR)
    val path = ServerContext.paths.staticResourcePath(newAvatarId, StaticResourcePurpose.USER_AVATAR)

    ServerContext.files.write(input, path)
    ServerContext.accounts.modifyAvatar(UUID.fromString(uidStr), newAvatarId)
    return path
}

private suspend fun deleteOldAvatar(uidStr: String?) {
    val oldAvatarId = ServerContext.accounts.getAvatar(UUID.fromString(uidStr))
    if (oldAvatarId != null) {
        val path = ServerContext.paths.staticResourcePath(oldAvatarId, StaticResourcePurpose.USER_AVATAR)
        ServerContext.files.delete(path)
    }
}