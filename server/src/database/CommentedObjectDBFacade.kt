package org.solvo.server.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.CommentableDownstream
import org.solvo.model.CommentableUpstream
import org.solvo.model.User
import org.solvo.server.ServerContext
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentedObjectDBFacade<T: CommentableUpstream> {
    suspend fun contains(coid: UUID): Boolean
    suspend fun post(content: T, author: User): UUID?
    suspend fun modifyContent(coid: UUID, content: String): Boolean
    suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): CommentableDownstream?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}

abstract class CommentedObjectDBFacadeImpl<T: CommentableUpstream> : CommentedObjectDBFacade<T> {
    abstract val associatedTable: Table

    override suspend fun post(content: T, author: User): UUID? = dbQuery {
        val coid = CommentedObjectTable.insertIgnoreAndGetId {
            it[CommentedObjectTable.author] = author.id
            it[CommentedObjectTable.content] = content.content
            it[CommentedObjectTable.anonymity] = content.anonymity
        }?.value ?: return@dbQuery null

        associateTableUpdates(coid, content, author)
    }

    protected abstract suspend fun associateTableUpdates(coid: UUID, content: T, author: User): UUID?

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

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}