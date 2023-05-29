package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.CommentableDownstream
import org.solvo.model.CommentableUpstream
import org.solvo.server.ServerContext
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentedObjectDBControl<T: CommentableUpstream> {
    suspend fun contains(coid: UUID): Boolean
    suspend fun post(content: T, authorId: UUID): UUID?
    suspend fun modifyContent(coid: UUID, content: String): Boolean
    suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): CommentableDownstream?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun dislike(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}

abstract class CommentedObjectDBControlImpl<T: CommentableUpstream> : CommentedObjectDBControl<T> {
    abstract val associatedTable: Table

    override suspend fun post(content: T, authorId: UUID): UUID? {
        val coid = dbQuery {
            CommentedObjectTable.insertIgnoreAndGetId {
                it[CommentedObjectTable.author] = authorId
                it[CommentedObjectTable.content] = content.content
                it[CommentedObjectTable.anonymity] = content.anonymity
            }?.value
        } ?: return null
        return dbQuery { associateTableUpdates(coid, content, authorId) }
    }

    protected abstract suspend fun associateTableUpdates(coid: UUID, content: T, authorId: UUID): UUID?

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !associatedTable.select(CommentedObjectTable.id eq coid).empty()
    }

    override suspend fun modifyContent(coid: UUID, content: String): Boolean = dbQuery {
        CommentedObjectTable.update({ CommentedObjectTable.id eq coid }) {
            it[CommentedObjectTable.content] = content
            it[CommentedObjectTable.lastEditTime] = ServerContext.localtime.now()
        } > 0
    }

    override suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean = dbQuery {
        CommentedObjectTable.update({ CommentedObjectTable.id eq coid }) {
            it[CommentedObjectTable.anonymity] = anonymity
        } > 0
    }

    override suspend fun delete(coid: UUID): Boolean = dbQuery {
        val success = CommentedObjectTable.deleteWhere { CommentedObjectTable.id eq coid } > 0
        if (success) {
            assert(associatedTable.deleteWhere { CommentedObjectTable.id eq coid } > 0)
        }
        success
    }

    abstract override suspend fun view(coid: UUID): CommentableDownstream?

    override suspend fun like(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun dislike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}