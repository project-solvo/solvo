package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.LongIdTable
import org.solvo.server.utils.TokenGenerator

object AuthTokenTable: LongIdTable() {
    val userId = reference("userId", UserTable)
    val token = varchar("token", TokenGenerator.TOKEN_SIZE).uniqueIndex()
}