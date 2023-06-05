package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AuthTokenTable
import java.util.*


interface AuthTokenDBControl {
    suspend fun addToken(uid: UUID, token: String): Boolean
    suspend fun matchToken(token: String): UUID?
    suspend fun getAllTokens(uid: UUID): List<String>
    suspend fun removeAllTokens(uid: UUID): Boolean
}

class AuthTokenDBControlImpl: AuthTokenDBControl {
    override suspend fun addToken(uid: UUID, token: String): Boolean = dbQuery {
        AuthTokenTable.insertIgnore {
            it[userId] = uid
            it[AuthTokenTable.token] = token
        }.resultedValues?.isNotEmpty() ?: false
    }

    override suspend fun matchToken(token: String): UUID? = dbQuery {
        AuthTokenTable.select { AuthTokenTable.token eq token }.map {
            it[AuthTokenTable.userId].value
        }.singleOrNull()
    }

    override suspend fun getAllTokens(uid: UUID): List<String> = dbQuery {
        AuthTokenTable.select { AuthTokenTable.userId eq uid }.map {
            it[AuthTokenTable.token]
        }
    }

    override suspend fun removeAllTokens(uid: UUID): Boolean = dbQuery {
        AuthTokenTable.deleteWhere { userId eq uid } > 0
    }
}