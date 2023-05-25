package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.solvo.model.Commentable
import org.solvo.server.ServerContext
import org.solvo.server.database.CommentedObjectDBFacade
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
    val newImageId = ServerContext.resources.addResource(purpose)
    val path = ServerContext.paths.staticResourcePath(newImageId, purpose)

    ServerContext.files.write(input, path)
    if (purpose == StaticResourcePurpose.USER_AVATAR) {
        ServerContext.accounts.modifyAvatar(uid, newImageId)
    }
    return path
}

suspend fun <T: Commentable> PipelineContext<Unit, ApplicationCall>.uploadNewContentConcerningAnonymity(
    commentable: T,
    uid: UUID,
    database: CommentedObjectDBFacade<T>,
): Boolean {
    commentable.author = ServerContext.accounts.getUserInfo(uid)

    val coid = database.post(commentable)
    return if (coid == null) {
        call.respond(HttpStatusCode.BadRequest)
        false
    } else {
        commentable.coid = coid
        if (commentable.anonymity) commentable.author = null
        true
    }
}