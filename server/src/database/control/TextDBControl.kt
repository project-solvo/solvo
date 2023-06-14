package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.TextTable
import java.util.*

interface TextDBControl {
    suspend fun post(content: String): UUID?
    suspend fun modify(id: UUID, content: String): Boolean
    suspend fun contains(id: UUID): Boolean
    suspend fun delete(id: UUID): Boolean
    suspend fun view(id: UUID): String?
}

class TextDBControlImpl : TextDBControl {
    override suspend fun post(content: String): UUID = dbQuery {
        TextTable.insertAndGetId {
            it[TextTable.content] = content
        }.value
    }

    override suspend fun modify(id: UUID, content: String): Boolean = dbQuery {
        TextTable
            .update({ TextTable.id eq id }) {
                it[TextTable.content] = content
            } > 0
    }

    override suspend fun contains(id: UUID): Boolean = dbQuery {
        !TextTable.select(TextTable.id eq id).empty()
    }

    override suspend fun view(id: UUID): String? = dbQuery {
        TextTable.select { TextTable.id eq id }
            .map { it[TextTable.content] }
            .singleOrNull()
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        TextTable.deleteWhere { TextTable.id eq id } > 0
    }
}