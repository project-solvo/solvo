package org.solvo.server.database

import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.DatabaseModel
import java.util.*

class AuthInfo(val userId: UUID, val username: String, val hash: ByteArray)

object AuthTable : Table("AuthInfo") {
    val userId = reference("userId", UserTable).uniqueIndex()
    val hash = binary("hash", DatabaseModel.HASH_SIZE)

    override val primaryKey = PrimaryKey(userId)
}
