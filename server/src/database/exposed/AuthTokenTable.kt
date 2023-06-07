package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table
import org.solvo.server.utils.TokenGenerator

object AuthTokenTable: Table() {
    val userId = reference("userId", UserTable)
    val token = varchar("token", TokenGenerator.TOKEN_SIZE).uniqueIndex()

    override val primaryKey = PrimaryKey(userId, token)
}