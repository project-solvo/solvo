package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteIgnoreWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.StaticResourceTable
import org.solvo.server.utils.StaticResourcePurpose
import java.util.*

interface ResourcesDBControl {
    suspend fun contains(resourceId: UUID): Boolean
    suspend fun addResource(purpose: StaticResourcePurpose, parentCOID: UUID? = null): UUID
    suspend fun getPurpose(resourceId: UUID): StaticResourcePurpose?
    suspend fun getParent(resourceId: UUID): UUID?
    suspend fun tryDeleteResource(resourceId: UUID): Boolean
}

class ResourcesDBControlImpl : ResourcesDBControl {
    override suspend fun contains(resourceId: UUID): Boolean = dbQuery {
        !StaticResourceTable.select(StaticResourceTable.id eq resourceId).empty()
    }

    override suspend fun addResource(purpose: StaticResourcePurpose, parentCOID: UUID?): UUID = dbQuery {
        StaticResourceTable.insertAndGetId {
            it[StaticResourceTable.purpose] = purpose
            it[coid] = parentCOID
        }.value
    }

    override suspend fun getPurpose(resourceId: UUID): StaticResourcePurpose? = dbQuery {
        StaticResourceTable
            .select(StaticResourceTable.id eq resourceId)
            .map { it[StaticResourceTable.purpose] }
            .singleOrNull()
    }

    override suspend fun getParent(resourceId: UUID): UUID? = dbQuery {
        StaticResourceTable
            .select(StaticResourceTable.id eq resourceId)
            .map { it[StaticResourceTable.coid]?.value }
            .singleOrNull()
    }

    override suspend fun tryDeleteResource(resourceId: UUID): Boolean = dbQuery {
        StaticResourceTable.deleteIgnoreWhere { StaticResourceTable.id eq resourceId } > 0
    }
}