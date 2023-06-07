package org.solvo.server.utils.sampleData

import org.solvo.model.api.communication.SharedContent
import org.solvo.server.ServerContext
import java.util.*

class SharedContentPostRequest(
    val content: String
) {
    lateinit var id: UUID
        private set

    suspend fun submit(
        db: ServerContext.Databases,
    ) {
        db.contents.apply {
            id = postSharedContent(SharedContent(content))!!
        }
    }
}