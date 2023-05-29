package org.solvo.server.database.control

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.CommentDownstream
import org.solvo.model.CommentUpstream
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentDBControl : CommentedObjectDBControl<CommentUpstream> {
    suspend fun post(content: CommentUpstream, authorId: UUID, parentID: UUID): UUID?
    suspend fun pin(uid: UUID, coid: UUID): Boolean
    suspend fun unpin(uid: UUID, coid: UUID): Boolean
    override suspend fun view(coid: UUID): CommentDownstream?
}

class CommentDBControlImpl(
    private val accountDB: AccountDBControl,
) : CommentDBControl, CommentedObjectDBControlImpl<CommentUpstream>() {
    override val associatedTable: Table = CommentTable

    override suspend fun post(content: CommentUpstream, authorId: UUID, parentID: UUID): UUID? {
        val coid = insertAndGetCOID(content, authorId) ?: return null

        dbQuery {
            assert(CommentTable.insert {
                it[CommentTable.coid] = coid
                it[CommentTable.parent] = parentID
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun view(coid: UUID): CommentDownstream? = dbQuery {
        CommentTable
            .join(CommentedObjectTable, JoinType.INNER, CommentTable.coid, CommentedObjectTable.id)
            .select(CommentTable.coid eq coid)
            .map {
                CommentDownstream(
                    coid = it[CommentTable.coid].value,
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content],
                    anonymity = it[CommentedObjectTable.anonymity],
                    likes = it[CommentedObjectTable.likes],
                    dislikes = it[CommentedObjectTable.dislikes],
                    parent = it[CommentTable.parent].value,
                    pinned = it[CommentTable.pinned],
                    subComments = listOf(), // TODO
                )
            }.singleOrNull()
    }

    override suspend fun pin(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unpin(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
