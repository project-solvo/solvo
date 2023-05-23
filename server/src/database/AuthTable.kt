package org.solvo.server.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.solvo.model.utils.DatabaseModel
import java.util.*

class AuthInfo(val userId: UUID, val username: String, val hash: ByteArray)

object AuthTable : UUIDTable("AuthInfo", "userId") {
    val username = varchar("username", DatabaseModel.USERNAME_MAX_LENGTH).uniqueIndex()
    val hash = binary("hash", DatabaseModel.HASH_SIZE)
}
