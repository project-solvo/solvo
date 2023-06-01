package org.solvo.server.utils.sampleData

import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File

class ImagePostRequest(
    val path: String,
    val user: UserRegisterRequest,
    val purpose: StaticResourcePurpose,
) {
    lateinit var url: String
        private set

    suspend fun submit(
        db: ServerContext.Databases,
    ) {
        db.contents.apply {
            val uid = user.uid
            val input = File(path).inputStream()
            val imageId = postImage(uid, input, purpose)
            url = ServerContext.paths.staticResourcePath(imageId, purpose)
        }
    }
}