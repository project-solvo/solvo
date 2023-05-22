package org.solvo.server.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.solvo.model.AccountChecker

class AuthInfo(val userId: Int, val username: String, val hash: ByteArray)

object AuthTable : IntIdTable() {
    val username = varchar("username", AccountChecker.USERNAME_MAX_LENGTH).uniqueIndex()
    val hash = binary("hash", AccountChecker.HASH_SIZE)
}
