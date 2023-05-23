package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

interface AccountDBFacade {
    suspend fun getId(username: String): UUID?
    suspend fun matchHash(id: UUID, hash: ByteArray): Boolean
    suspend fun addAuth(username: String, hash: ByteArray): UUID?
    suspend fun modifyAuth(id: UUID, username: String, hash: ByteArray): Boolean
    suspend fun deleteAuth(id: UUID): Boolean
}

class AccountDBFacadeImpl : AccountDBFacade {
    override suspend fun getId(username: String): UUID? = DatabaseFactory.dbQuery {
        UserTable
            .select(UserTable.username eq username)
            .map { it[UserTable.id].value }
            .singleOrNull()
    }

    override suspend fun matchHash(id: UUID, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable
            .select(AuthTable.userId eq id)
            .map { it[AuthTable.hash] }
            .singleOrNull()
    }.contentEquals(hash)

    override suspend fun addAuth(username: String, hash: ByteArray): UUID? = DatabaseFactory.dbQuery {
        val userId = UserTable.insert {
            it[UserTable.username] = username
        }.resultedValues?.singleOrNull()?.let { it[UserTable.id].value }
        if (userId != null) {
            AuthTable.insert {
                it[AuthTable.userId] = userId
                it[AuthTable.hash] = hash
            }
        }
        userId
    }

    override suspend fun modifyAuth(id: UUID, username: String, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        if (UserTable.update ({ UserTable.id eq id }) {
            it[UserTable.username] = username
        } == 0) return@dbQuery false
        AuthTable.update ({ AuthTable.userId eq id }) {
            it[AuthTable.hash] = hash
        } > 0
    }

    override suspend fun deleteAuth(id: UUID): Boolean = DatabaseFactory.dbQuery {
        if (UserTable.deleteWhere { UserTable.id eq id } == 0) return@dbQuery false
        AuthTable.deleteWhere { AuthTable.userId eq id } > 0
    }
}