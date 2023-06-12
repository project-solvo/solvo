package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.api.communication.*
import org.solvo.model.utils.ModelConstraints
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AnswerCodeTable
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface CommentDBControl : CommentedObjectDBControl<CommentUpstream> {
    suspend fun post(content: CommentUpstream, authorId: UUID, parentID: UUID, kind: CommentKind): UUID?
    suspend fun pin(uid: UUID, coid: UUID): Boolean
    suspend fun unpin(uid: UUID, coid: UUID): Boolean
    override suspend fun view(coid: UUID): CommentDownstream?
}

class CommentDBControlImpl(
    private val accountDB: AccountDBControl,
) : CommentDBControl, CommentedObjectDBControlImpl<CommentUpstream>() {
    override val associatedTable = CommentTable

    override suspend fun post(content: CommentUpstream, authorId: UUID, parentID: UUID, kind: CommentKind): UUID? {
        val coid = insertAndGetCOID(content, authorId) ?: return null

        dbQuery {
            assert(CommentTable.insert {
                it[CommentTable.coid] = coid
                it[CommentTable.parent] = parentID
                it[CommentTable.kind] = kind
            }.resultedValues?.singleOrNull() != null)
        }

        if (kind.isAnswerOrThought()) dbQuery {
            val code = AnswerCodeTable
                .select(AnswerCodeTable.parent eq parentID)
                .maxOfOrNull { it[AnswerCodeTable.code] }?.inc() ?: 1
            assert(AnswerCodeTable.insert {
                it[AnswerCodeTable.coid] = coid
                it[AnswerCodeTable.parent] = parentID
                it[AnswerCodeTable.code] = code
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun view(coid: UUID): CommentDownstream? = dbQuery {
        val subCommentTable = CommentTable.alias("SubComments")
        val query = CommentTable
            .join(subCommentTable, JoinType.INNER, CommentTable.coid, subCommentTable[CommentTable.parent])
            .join(CommentedObjectTable, JoinType.INNER, subCommentTable[CommentTable.coid], CommentedObjectTable.id)
            .select(CommentTable.coid eq coid)
            .orderBy(
                // TODO: better strategy
                Pair(CommentedObjectTable.likes, SortOrder.DESC), Pair(CommentedObjectTable.postTime, SortOrder.DESC)
            )
        val previewSubComments: List<LightCommentDownstream> = query
            .take(ModelConstraints.LIGHT_SUB_COMMENTS_AMOUNT)
            .map {
                LightCommentDownstream(
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content],
                )
            }

        val subCommentIds: List<UUID> = query.map { it[subCommentTable[CommentTable.coid]].value }

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
                    postTime = it[CommentedObjectTable.postTime],
                    lastEditTime = it[CommentedObjectTable.lastEditTime],
                    lastCommentTime = it[CommentedObjectTable.lastCommentTime],
                    previewSubComments = previewSubComments,
                    allSubCommentIds = subCommentIds,
                    answerCode = if (!it[CommentTable.kind].isAnswerOrThought()) null else {
                        AnswerCodeTable
                            .select(AnswerCodeTable.coid eq coid)
                            .map { answerRow -> answerRow[AnswerCodeTable.code] }
                            .singleOrNull()
                    },
                    kind = it[CommentTable.kind],
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
