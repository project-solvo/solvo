package org.solvo.server.utils.sampleData.builder

import org.solvo.server.ServerContext
import java.util.*

class UserRegisterRequest(
    val username: String,
    val password: ByteArray,
) {
    lateinit var uid: UUID
        private set
    suspend fun submit(db: ServerContext.Databases) {
        db.accounts.apply {
            register(username, password)
            val token = login(username, password).token
            uid = ServerContext.tokens.matchToken(token)!!
        }
    }
}