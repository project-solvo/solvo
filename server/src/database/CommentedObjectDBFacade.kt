package org.solvo.server.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.solvo.model.Commentable
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentedObjectDBFacade<T : Commentable> {
    suspend fun contains(coid: UUID): Boolean
    suspend fun post(content: T): UUID?
    suspend fun modifyContent(coid: UUID, content: String): Boolean
    suspend fun setAnonymity(coid: UUID, anonymity: Boolean): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): T?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}

abstract class CommentedObjectDBFacadeImpl<T : Commentable> : CommentedObjectDBFacade<T> {
    abstract val associatedTable: Table

    override suspend fun post(content: T): UUID? = dbQuery {
        val coid = CommentedObjectTable.insertIgnoreAndGetId {
            it[CommentedObjectTable.author] = content.author!!.id
            it[CommentedObjectTable.content] = content.content
            it[CommentedObjectTable.anonymity] = content.anonymity
        }?.value ?: return@dbQuery null

        val args = associateTableArgsCompute(content)
        assert(associatedTable.insert {
            associateTableUpdates(it, coid, content, args)
        }.resultedValues?.singleOrNull() != null)

        coid
    }

    protected open suspend fun associateTableArgsCompute(content: T): List<Any?> { return listOf() }

    protected abstract fun associateTableUpdates(
        it: InsertStatement<Number>,
        coid: UUID,
        content: T,
        args: List<Any?>,
    )


    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !associatedTable.select(CommentedObjectTable.id eq coid).empty()
    }

    override suspend fun modifyContent(coid: UUID, content: String): Boolean = dbQuery {
        CommentedObjectTable.update({ CommentedObjectTable.id eq coid }) {
            it[CommentedObjectTable.content] = content
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

    abstract override suspend fun view(coid: UUID): T?

    override suspend fun like(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}