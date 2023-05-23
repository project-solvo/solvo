package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

interface AuthTableFacade {
    suspend fun getId(username: String): UUID?
    suspend fun matchHash(id: UUID, hash: ByteArray): Boolean
    suspend fun addAuth(username: String, hash: ByteArray): UUID?
    suspend fun modifyAuth(id: UUID, username: String, hash: ByteArray): Boolean
    suspend fun deleteAuth(id: UUID): Boolean
}

class AuthTableFacadeImpl : AuthTableFacade {
    override suspend fun getId(username: String): UUID? = DatabaseFactory.dbQuery {
        AuthTable
            .select(AuthTable.username eq username)
            .map { it[AuthTable.id].value }
            .singleOrNull()
    }

    override suspend fun matchHash(id: UUID, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable
            .select(AuthTable.id eq id)
            .map { it[AuthTable.hash] }
            .singleOrNull()
    }.contentEquals(hash)

    override suspend fun addAuth(username: String, hash: ByteArray): UUID? = DatabaseFactory.dbQuery {
        val insertStatement = AuthTable.insert {
            it[AuthTable.username] = username
            it[AuthTable.hash] = hash
        }
        insertStatement.resultedValues?.singleOrNull()?.let { it[AuthTable.id].value }
    }

    override suspend fun modifyAuth(id: UUID, username: String, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable.update ({ AuthTable.id eq id }) {
            it[AuthTable.username] = username
            it[AuthTable.hash] = hash
        } > 0
    }

    override suspend fun deleteAuth(id: UUID): Boolean = DatabaseFactory.dbQuery {
        AuthTable.deleteWhere { AuthTable.id eq id } > 0
    }
}