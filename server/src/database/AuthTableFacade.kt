package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

interface AuthTableFacade {
    suspend fun getId(username: String): Int?
    suspend fun matchHash(id: Int, hash: ByteArray): Boolean
    suspend fun addAuth(username: String, hash: ByteArray): Int?
    suspend fun modifyAuth(id: Int, username: String, hash: ByteArray): Boolean
    suspend fun deleteAuth(id: Int): Boolean
}

class AuthTableFacadeImpl : AuthTableFacade {
    override suspend fun getId(username: String): Int? = DatabaseFactory.dbQuery {
        AuthTable
            .select(AuthTable.username eq username)
            .map { it[AuthTable.id].value }
            .singleOrNull()
    }

    override suspend fun matchHash(id: Int, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable
            .select(AuthTable.id eq id)
            .map { it[AuthTable.hash] }
            .singleOrNull()
    }.contentEquals(hash)

    override suspend fun addAuth(username: String, hash: ByteArray): Int? = DatabaseFactory.dbQuery {
        val insertStatement = AuthTable.insert {
            it[AuthTable.username] = username
            it[AuthTable.hash] = hash
        }
        insertStatement.resultedValues?.singleOrNull()?.let { it[AuthTable.id].value }
    }

    override suspend fun modifyAuth(id: Int, username: String, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable.update ({ AuthTable.id eq id }) {
            it[AuthTable.username] = username
            it[AuthTable.hash] = hash
        } > 0
    }

    override suspend fun deleteAuth(id: Int): Boolean = DatabaseFactory.dbQuery {
        AuthTable.deleteWhere { AuthTable.id eq id } > 0
    }
}