package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.CommentDownstream
import org.solvo.model.CommentUpstream
import org.solvo.model.FullCommentDownstream
import org.solvo.model.LightCommentDownstream
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentDBControl : CommentedObjectDBControl<CommentUpstream> {
    suspend fun post(content: CommentUpstream, authorId: UUID, parentID: UUID, asAnswer: Boolean = false): UUID?
    suspend fun pin(uid: UUID, coid: UUID): Boolean
    suspend fun unpin(uid: UUID, coid: UUID): Boolean
    override suspend fun view(coid: UUID): CommentDownstream?
    suspend fun viewFull(coid: UUID): FullCommentDownstream?
}

class CommentDBControlImpl(
    private val accountDB: AccountDBControl,
) : CommentDBControl, CommentedObjectDBControlImpl<CommentUpstream>() {
    override val associatedTable: Table = CommentTable

    override suspend fun post(content: CommentUpstream, authorId: UUID, parentID: UUID, asAnswer: Boolean): UUID? {
        val coid = insertAndGetCOID(content, authorId) ?: return null

        dbQuery {
            assert(CommentTable.insert {
                it[CommentTable.coid] = coid
                it[CommentTable.parent] = parentID
                it[CommentTable.asAnswer] = asAnswer
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun view(coid: UUID): CommentDownstream? = dbQuery {
        val subCommentTable = CommentTable.alias("SubComments")
        val subComments: List<LightCommentDownstream> = CommentTable
            .join(subCommentTable, JoinType.INNER, CommentTable.coid, subCommentTable[CommentTable.parent])
            .join(CommentedObjectTable, JoinType.INNER, subCommentTable[CommentTable.coid], CommentedObjectTable.id)
            .select(CommentTable.coid eq coid)
            .orderBy(
                // TODO: better strategy
                Pair(CommentedObjectTable.likes, SortOrder.DESC), Pair(CommentedObjectTable.postTime, SortOrder.DESC)
            )
            .take(DatabaseModel.LIGHT_SUB_COMMENTS_AMOUNT)
            .map { LightCommentDownstream(
                author = if (it[subCommentTable[CommentedObjectTable.anonymity]]) {
                    null
                } else {
                    accountDB.getUserInfo(it[subCommentTable[CommentedObjectTable.author]].value)!!
                },
                content = it[subCommentTable[CommentedObjectTable.content]],
            ) }

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
                    subComments = subComments,
                )
            }.singleOrNull()
    }

    override suspend fun viewFull(coid: UUID): FullCommentDownstream? = dbQuery {
        val subComments: List<UUID> = CommentTable
            .join(CommentTable, JoinType.INNER, CommentTable.coid, CommentTable.parent)
            .select(CommentTable.coid eq coid)
            .map { it[CommentTable.coid].value }

        CommentTable
            .join(CommentedObjectTable, JoinType.INNER, CommentTable.coid, CommentedObjectTable.id)
            .select(CommentTable.coid eq coid)
            .map {
                FullCommentDownstream(
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
                    subComments = subComments,
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
