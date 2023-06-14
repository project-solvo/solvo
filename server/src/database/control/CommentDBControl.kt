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
    suspend fun edit(request: CommentEditRequest, coid: UUID): Boolean
    suspend fun getParentId(coid: UUID): UUID?
    suspend fun pin(uid: UUID, coid: UUID): Boolean
    suspend fun unpin(uid: UUID, coid: UUID): Boolean
    suspend fun view(coid: UUID, uid: UUID?): CommentDownstream?
}

class CommentDBControlImpl(
    private val accountDB: AccountDBControl,
    private val textDB: TextDBControl,
) : CommentDBControl, CommentedObjectDBControlImpl<CommentUpstream>(textDB) {
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

    override suspend fun edit(request: CommentEditRequest, coid: UUID): Boolean = dbQuery {
        if (!contains(coid)) return@dbQuery false
        request.run {
            anonymity?.let { anonymity -> setAnonymity(coid, anonymity) }
            content?.let { content -> modifyContent(coid, content.str) }
        }
        return@dbQuery true
    }

    override suspend fun view(coid: UUID, uid: UUID?): CommentDownstream? = dbQuery {
        if (!contains(coid)) return@dbQuery null
        val subCommentTable = CommentTable.alias("SubComments")
        val query = CommentTable
            .join(subCommentTable, JoinType.INNER, CommentTable.coid, subCommentTable[CommentTable.parent])
            .join(CommentedObjectTable, JoinType.INNER, subCommentTable[CommentTable.coid], CommentedObjectTable.id)
            .select(CommentTable.coid eq coid)
            .orderBy(
                // TODO: better strategy
                Pair(CommentedObjectTable.postTime, SortOrder.DESC)
            )
            .filter { it[subCommentTable[CommentTable.visible]] }
        val previewSubComments: List<LightCommentDownstream> = query
            .take(ModelConstraints.LIGHT_SUB_COMMENTS_AMOUNT)
            .map {
                LightCommentDownstream(
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content].value.let { textId ->
                        textDB.view(textId) ?: error("text not found with id $textId")
                    },
                )
            }

        val subCommentIds: List<UUID> = query.map { it[subCommentTable[CommentTable.coid]].value }

        CommentTable
            .join(CommentedObjectTable, JoinType.INNER, CommentTable.coid, CommentedObjectTable.id)
            .select(CommentTable.coid eq coid)
            .map {
                val author = accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                CommentDownstream(
                    coid = it[CommentTable.coid].value,
                    author = if (it[CommentedObjectTable.anonymity]) null else author,
                    content = it[CommentedObjectTable.content].value.let { textId ->
                        textDB.view(textId) ?: error("text not found with id $textId")
                    },
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
                    isSelf = uid != null && author.id == uid,
                )
            }.singleOrNull()
    }

    override suspend fun getParentId(coid: UUID): UUID? = dbQuery {
        CommentTable
            .select(CommentTable.coid eq coid)
            .filter { it[CommentTable.visible] }
            .map { it[CommentTable.parent].value }
            .singleOrNull()
    }

    override suspend fun pin(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unpin(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
