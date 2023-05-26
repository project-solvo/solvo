package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import java.io.InputStream
import java.util.*


suspend fun PipelineContext<Unit, ApplicationCall>.getUserId(): UUID? {
    val uidStr = call.principal<UserIdPrincipal>()?.name
    if (uidStr == null) {
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }
    return UUID.fromString(uidStr)
}

suspend fun PipelineContext<Unit, ApplicationCall>.matchUserId(matchUidStr: String?): UUID? {
    val uidStr = call.principal<UserIdPrincipal>()?.name
    if (uidStr == null || matchUidStr != uidStr) {
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }
    return UUID.fromString(uidStr)
}

suspend fun uploadNewImage(uid: UUID, input: InputStream, purpose: StaticResourcePurpose): String {
    val newImageId = ServerContext.Databases.resources.addResource(purpose)
    val path = ServerContext.paths.staticResourcePath(newImageId, purpose)

    ServerContext.files.write(input, path)
    if (purpose == StaticResourcePurpose.USER_AVATAR) {
        ServerContext.Databases.accounts.modifyAvatar(uid, newImageId)
    }
    return path
}
