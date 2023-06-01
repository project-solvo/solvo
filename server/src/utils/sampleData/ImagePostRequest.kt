package org.solvo.server.utils.sampleData

import io.ktor.http.*
import org.solvo.server.ServerContext
import org.solvo.server.utils.ServerPathType
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File

class ImagePostRequest(
    val path: String,
    val contentType: ContentType = ContentType.Any,
    val user: UserRegisterRequest,
    val purpose: StaticResourcePurpose,
) {
    lateinit var url: String
        private set

    suspend fun submit(
        db: ServerContext.Databases,
    ) {
        db.resources.apply {
            val uid = user.uid
            val input = File(path).inputStream()
            val imageId = postImage(uid, input, purpose, contentType)
            url = ServerContext.paths.resolveResourcePath(imageId, purpose, ServerPathType.REMOTE)
        }
    }
}