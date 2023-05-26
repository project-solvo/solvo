package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.StaticResourceTable
import org.solvo.server.utils.StaticResourcePurpose
import java.util.*

interface ResourcesDBFacade {
    suspend fun contains(resourceId: UUID): Boolean
    suspend fun addResource(purpose: StaticResourcePurpose, parentCOID: UUID? = null): UUID
    suspend fun getPurpose(resourceId: UUID): StaticResourcePurpose?
    suspend fun getParent(resourceId: UUID): UUID?
    suspend fun deleteResource(resourceId: UUID): Boolean
}

class ResourcesDBFacadeImpl : ResourcesDBFacade {
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

    override suspend fun deleteResource(resourceId: UUID): Boolean = dbQuery {
        StaticResourceTable.deleteWhere { StaticResourceTable.id eq resourceId } > 0
    }
}