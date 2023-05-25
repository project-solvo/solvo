package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.Commentable
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentedObjectDBFacade<T: Commentable> {
    suspend fun contains(coid: UUID): Boolean
    suspend fun post(content: T): UUID?
    suspend fun modify(content: T): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): T?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}

class CommentedObjectDBFacadeImpl<T: Commentable>: CommentedObjectDBFacade<T> {
    override suspend fun post(content: T): UUID? = dbQuery {
        CommentedObjectTable.insert {
            it[CommentedObjectTable.author] = content.author!!.id
            it[CommentedObjectTable.content] = content.content
            it[CommentedObjectTable.anonymity] = content.anonymity
        }.resultedValues?.map { it[CommentedObjectTable.id].value }?.singleOrNull()
    }

    override suspend fun modify(content: T): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !CommentedObjectTable.select(CommentedObjectTable.id eq coid).empty()
    }

    override suspend fun delete(coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun view(coid: UUID): T? {
        TODO("Not yet implemented")
    }

    override suspend fun like(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}