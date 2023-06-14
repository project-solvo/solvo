package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.solvo.model.api.communication.CommentableUpstream
import org.solvo.server.ServerContext
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.COIDTable
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentedObjectDBControl<T : CommentableUpstream> {
    suspend fun contains(coid: UUID): Boolean
    suspend fun modifyContent(coid: UUID, content: String): Boolean
    suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean
    suspend fun getAuthorId(coid: UUID): UUID?
    suspend fun delete(coid: UUID): Boolean
}

abstract class CommentedObjectDBControlImpl<T : CommentableUpstream>(
    private val textDB: TextDBControl,
) : CommentedObjectDBControl<T> {
    abstract val associatedTable: COIDTable

    protected suspend fun insertAndGetCOID(content: T, authorId: UUID): UUID? {
        val contentId = textDB.post(content.content.str) ?: return null
        return dbQuery {
            CommentedObjectTable.insertIgnoreAndGetId {
                it[CommentedObjectTable.author] = authorId
                it[CommentedObjectTable.content] = contentId
                it[CommentedObjectTable.anonymity] = content.anonymity
            }?.value
        }
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !associatedTable.select(associatedTable.coid eq coid).empty()
    }

    override suspend fun modifyContent(coid: UUID, content: String): Boolean {
        val newContentId = textDB.post(content) ?: return false
        return dbQuery {
            CommentedObjectTable.update({ CommentedObjectTable.id eq coid }) {
                it[CommentedObjectTable.content] = newContentId
                it[CommentedObjectTable.lastEditTime] = ServerContext.localtime.now()
            } > 0
        }
    }

    override suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean = dbQuery {
        CommentedObjectTable.update({ CommentedObjectTable.id eq coid }) {
            it[CommentedObjectTable.anonymity] = anonymity
        } > 0
    }

    override suspend fun getAuthorId(coid: UUID): UUID? = dbQuery {
        CommentedObjectTable
            .select(CommentedObjectTable.id eq coid)
            .map { it[CommentedObjectTable.author].value }
            .singleOrNull()
    }

    override suspend fun delete(coid: UUID): Boolean = dbQuery {
        val success = CommentedObjectTable.deleteWhere { CommentedObjectTable.id eq coid } > 0
        if (success) {
            assert(associatedTable.deleteWhere { associatedTable.coid eq coid } > 0)
        }
        success
    }
}