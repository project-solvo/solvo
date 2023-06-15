package org.solvo.server.utils.sampleData.builder

import io.ktor.http.*
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.util.*

class ImagePostRequest(
    val path: String,
    val contentType: ContentType = ContentType.Any,
    val user: UserRegisterRequest,
    val purpose: StaticResourcePurpose,
) {
    lateinit var url: String
        private set

    lateinit var id: UUID
        private set

    suspend fun submit(
        db: ServerContext.Databases,
    ) {
        db.resources.apply {
            val uid = user.uid
            val input = File(path).inputStream()
            id = postImage(uid, input, purpose, contentType)
            url = ServerContext.paths.resolveRelativeResourcePath(id, purpose)
        }
    }
}