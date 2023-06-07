package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.solvo.model.api.communication.SharedContent
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.SharedContentTable
import java.util.*

interface SharedContentDBControl {
    suspend fun post(content: SharedContent): UUID?
    suspend fun modify(id: UUID, content: SharedContent): Boolean
    suspend fun delete(id: UUID): Boolean
    suspend fun view(id: UUID): SharedContent?
}

class SharedContentDBControlImpl : SharedContentDBControl {
    override suspend fun post(content: SharedContent): UUID? = dbQuery {
        SharedContentTable.insertAndGetId {
            it[SharedContentTable.content] = content.content
        }.value
    }

    override suspend fun modify(id: UUID, content: SharedContent): Boolean = dbQuery {
        SharedContentTable
            .update({ SharedContentTable.id eq id }) {
                it[SharedContentTable.content] = content.content
            } > 0
    }

    override suspend fun view(id: UUID): SharedContent? = dbQuery {
        SharedContentTable.select { SharedContentTable.id eq id }
            .map { SharedContent(it[SharedContentTable.content]) }
            .singleOrNull()
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        SharedContentTable.deleteWhere { SharedContentTable.id eq id } > 0
    }
}