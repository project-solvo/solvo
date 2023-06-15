package org.solvo.server.utils.sampleData.builder

import io.ktor.http.*
import org.solvo.server.ServerContext
import org.solvo.server.utils.sampleData.SampleDataDslMarker
import java.io.File
import java.util.*

class UserRegisterRequest(
    val username: String,
    val password: ByteArray,
    val avatarpath: String?,
    val avatarContentType: ContentType,
) {
    lateinit var uid: UUID
        private set

    suspend fun submit(db: ServerContext.Databases) {
        db.accounts.apply {
            register(username, password)
            val token = login(username, password).token!!
            uid = ServerContext.tokens.matchToken(token)!!
        }
        if (avatarpath != null) {
            db.resources.apply {
                val input = File(avatarpath).inputStream()
                uploadNewAvatar(uid, input, avatarContentType)
            }
        }
    }
}

@SampleDataDslMarker
class UserDataBuilder(
    private val username: String,
    private val password: ByteArray
) {
    private var avatarPath: String? = null
    private var avatarContentType: ContentType = ContentType.Any

    fun avatar(path: String, contentType: ContentType) {
        avatarPath = path
        avatarContentType = contentType
    }

    fun build(): UserRegisterRequest {
        return UserRegisterRequest(username, password, avatarPath, avatarContentType)
    }
}