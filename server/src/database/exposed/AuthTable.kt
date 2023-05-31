package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.ModelConstraints
import java.util.*

class AuthInfo(val userId: UUID, val username: String, val hash: ByteArray)

object AuthTable : Table("AuthInfo") {
    val userId = reference("userId", UserTable).uniqueIndex()
    val hash = binary("hash", ModelConstraints.HASH_SIZE)

    override val primaryKey = PrimaryKey(userId)
}
