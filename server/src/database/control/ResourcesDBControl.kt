package org.solvo.server.database.control

import io.ktor.http.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.StaticResourceTable
import org.solvo.server.utils.StaticResourcePurpose
import java.util.*

interface ResourcesDBControl {
    suspend fun contains(resourceId: UUID): Boolean
    suspend fun addResource(purpose: StaticResourcePurpose, contentType: ContentType, parentCOID: UUID? = null): UUID
    suspend fun getPurpose(resourceId: UUID): StaticResourcePurpose?
    suspend fun getContentType(resourceId: UUID): ContentType
    suspend fun getParent(resourceId: UUID): UUID?
    suspend fun tryDeleteResource(resourceId: UUID): Boolean
}

class ResourcesDBControlImpl : ResourcesDBControl {
    override suspend fun contains(resourceId: UUID): Boolean = dbQuery {
        !StaticResourceTable.select(StaticResourceTable.id eq resourceId).empty()
    }

    override suspend fun addResource(
        purpose: StaticResourcePurpose,
        contentType: ContentType,
        parentCOID: UUID?
    ): UUID = dbQuery {
        StaticResourceTable.insertAndGetId {
            it[StaticResourceTable.purpose] = purpose
            it[StaticResourceTable.contentType] = contentType.toString()
            it[coid] = parentCOID
        }.value
    }

    override suspend fun getPurpose(resourceId: UUID): StaticResourcePurpose? = dbQuery {
        StaticResourceTable
            .select(StaticResourceTable.id eq resourceId)
            .map { it[StaticResourceTable.purpose] }
            .singleOrNull()
    }

    override suspend fun getContentType(resourceId: UUID): ContentType = dbQuery {
        StaticResourceTable
            .select(StaticResourceTable.id eq resourceId)
            .map { ContentType.parse(it[StaticResourceTable.contentType]) }
            .singleOrNull()
            ?: ContentType.Any
    }

    override suspend fun getParent(resourceId: UUID): UUID? = dbQuery {
        StaticResourceTable
            .select(StaticResourceTable.id eq resourceId)
            .map { it[StaticResourceTable.coid]?.value }
            .singleOrNull()
    }

    override suspend fun tryDeleteResource(resourceId: UUID): Boolean = dbQuery {
        try {
            StaticResourceTable.deleteWhere { StaticResourceTable.id eq resourceId } > 0
        } catch (_: Exception) {
            false
        }
    }
}