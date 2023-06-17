package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.solvo.model.api.communication.*
import org.solvo.model.utils.ModelConstraints
import org.solvo.model.utils.NonBlankString
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import org.solvo.server.database.exposed.QuestionTable
import org.solvo.server.database.updateIfNotEmpty
import java.util.*

interface QuestionDBControl : CommentedObjectDBControl<QuestionUpstream> {
    suspend fun post(content: QuestionUpstream, authorId: UUID, articleId: UUID, code: NonBlankString): UUID?
    suspend fun create(authorId: UUID, articleId: UUID, code: NonBlankString): UUID?
    suspend fun edit(request: QuestionEditRequest, userId: UUID, questionId: UUID): Boolean
    suspend fun getId(articleId: UUID, code: String): UUID?
    suspend fun view(coid: UUID): QuestionDownstream?
}

class QuestionDBControlImpl(
    private val accountDB: AccountDBControl,
    private val textDB: TextDBControl,
) : QuestionDBControl, CommentedObjectDBControlImpl<QuestionUpstream>(textDB) {
    override val associatedTable = QuestionTable

    override suspend fun getId(articleId: UUID, code: String): UUID? = dbQuery {
        QuestionTable
            .select((QuestionTable.article eq articleId) and (QuestionTable.code eq code))
            .filter { it[QuestionTable.visible] }
            .map { it[QuestionTable.coid].value }
            .singleOrNull()
    }

    override suspend fun post(content: QuestionUpstream, authorId: UUID, articleId: UUID, code: NonBlankString): UUID? {
        if (code.str.length > ModelConstraints.QUESTION_CODE_MAX_LENGTH) return null
        if (content.sharedContent?.let { textDB.contains(it) } == false) return null
        val coid = insertAndGetCOID(content, authorId) ?: return null
        dbQuery {
            assert(QuestionTable.insert {
                it[QuestionTable.coid] = coid
                it[QuestionTable.sharedContent] = content.sharedContent
                it[QuestionTable.article] = articleId
                it[QuestionTable.code] = code.str
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun create(authorId: UUID, articleId: UUID, code: NonBlankString): UUID? {
        return post(QuestionUpstream(), authorId, articleId, code)
    }

    override suspend fun edit(request: QuestionEditRequest, userId: UUID, questionId: UUID): Boolean = dbQuery {
        if (!contains(questionId)) return@dbQuery false

        request.run {
            anonymity?.let { anonymity -> setAnonymity(questionId, anonymity) }
            content?.let { content -> modifyContent(questionId, content.str) }
            QuestionTable.updateIfNotEmpty({ QuestionTable.coid eq questionId }) {
                request.code?.let { code -> it[QuestionTable.code] = code.str }
            }

            return@dbQuery true
        }
    }

    override suspend fun view(coid: UUID): QuestionDownstream? = dbQuery {
        if (!contains(coid)) return@dbQuery null

        val answers: List<UUID> = QuestionTable
            .join(CommentTable, JoinType.INNER, QuestionTable.coid, CommentTable.parent)
            .select((QuestionTable.coid eq coid) and (CommentTable.kind.isAnswerOrThought()))
            .filter { it[CommentTable.visible] }
            .map { it[CommentTable.coid].value }

        val comments: List<UUID> = QuestionTable
            .join(CommentTable, JoinType.INNER, QuestionTable.coid, CommentTable.parent)
            .select((QuestionTable.coid eq coid) and (CommentTable.kind eq CommentKind.COMMENT))
            .filter { it[CommentTable.visible] }
            .map { it[CommentTable.coid].value }

        QuestionTable
            .join(CommentedObjectTable, JoinType.INNER, QuestionTable.coid, CommentedObjectTable.id)
            .select(QuestionTable.coid eq coid)
            .map {
                val sharedContent = it[QuestionTable.sharedContent]?.value?.let { contentId ->
                    textDB.view(contentId)
                }

                QuestionDownstream(
                    coid = it[QuestionTable.coid].value,
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content].value.let { textId ->
                        textDB.view(textId) ?: error("text not found with id $textId")
                    },
                    anonymity = it[CommentedObjectTable.anonymity],
                    likes = it[CommentedObjectTable.likes],
                    dislikes = it[CommentedObjectTable.dislikes],
                    sharedContent = sharedContent?.let { text ->
                        SharedContent(text)
                    } ?: SharedContent.nullContent,
                    code = it[QuestionTable.code],
                    article = it[QuestionTable.article].value,
                    answers = answers,
                    comments = comments,
                )
            }.singleOrNull()
    }
}

private fun ExpressionWithColumnType<CommentKind>.isAnswerOrThought(): Op<Boolean> {
    return this neq CommentKind.COMMENT
}
