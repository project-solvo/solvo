package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.solvo.model.CommentableDownstream
import org.solvo.model.CommentableUpstream
import org.solvo.server.ServerContext
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.COIDTable
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentedObjectDBControl<T : CommentableUpstream> {
    suspend fun contains(coid: UUID): Boolean
    suspend fun modifyContent(coid: UUID, content: String): Boolean
    suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): CommentableDownstream?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun dislike(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}

abstract class CommentedObjectDBControlImpl<T : CommentableUpstream> : CommentedObjectDBControl<T> {
    abstract val associatedTable: COIDTable

    protected suspend fun insertAndGetCOID(content: T, authorId: UUID): UUID? = dbQuery {
        CommentedObjectTable.insertIgnoreAndGetId {
            it[CommentedObjectTable.author] = authorId
            it[CommentedObjectTable.content] = content.content
            it[CommentedObjectTable.anonymity] = content.anonymity
        }?.value
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !associatedTable.select(associatedTable.coid eq coid).empty()
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
            assert(associatedTable.deleteWhere { associatedTable.coid eq coid } > 0)
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