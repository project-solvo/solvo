package org.solvo.server.database.control

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.QuestionDownstream
import org.solvo.model.QuestionUpstream
import org.solvo.model.utils.ModelConstraints
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface QuestionDBControl : CommentedObjectDBControl<QuestionUpstream> {
    suspend fun post(content: QuestionUpstream, authorId: UUID, articleId: UUID): UUID?
    suspend fun getId(articleId: UUID, code: String): UUID?
    override suspend fun view(coid: UUID): QuestionDownstream?
}

class QuestionDBControlImpl(
    private val commentDB: CommentDBControl,
    private val accountDB: AccountDBControl,
) : QuestionDBControl, CommentedObjectDBControlImpl<QuestionUpstream>() {
    override val associatedTable = QuestionTable

    override suspend fun getId(articleId: UUID, code: String): UUID? = dbQuery {
        QuestionTable
            .select((QuestionTable.article eq articleId) and (QuestionTable.code eq code))
            .map { it[QuestionTable.coid].value }
            .singleOrNull()
    }

    override suspend fun post(content: QuestionUpstream, authorId: UUID, articleId: UUID): UUID? {
        if (content.code.length > ModelConstraints.QUESTION_CODE_MAX_LENGTH) return null
        val coid = insertAndGetCOID(content, authorId) ?: return null
        dbQuery {
            assert(QuestionTable.insert {
                it[QuestionTable.coid] = coid
                it[QuestionTable.article] = articleId
                it[QuestionTable.code] = content.code
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun view(coid: UUID): QuestionDownstream? = dbQuery {
        val answers: List<UUID> = QuestionTable
            .join(CommentTable, JoinType.INNER, QuestionTable.coid, CommentTable.parent)
            .select((QuestionTable.coid eq coid) and (CommentTable.asAnswer eq true))
            .map { it[CommentTable.coid].value }

        val comments: List<UUID> = QuestionTable
            .join(CommentTable, JoinType.INNER, QuestionTable.coid, CommentTable.parent)
            .select((QuestionTable.coid eq coid) and (CommentTable.asAnswer eq false))
            .map { it[CommentTable.coid].value }

        QuestionTable
            .join(CommentedObjectTable, JoinType.INNER, QuestionTable.coid, CommentedObjectTable.id)
            .select(QuestionTable.coid eq coid)
            .map {
                QuestionDownstream(
                    coid = it[QuestionTable.coid].value,
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content],
                    anonymity = it[CommentedObjectTable.anonymity],
                    likes = it[CommentedObjectTable.likes],
                    dislikes = it[CommentedObjectTable.dislikes],
                    code = it[QuestionTable.code],
                    article = it[QuestionTable.article].value,
                    answers = answers,
                    comments = comments,
                )
            }.singleOrNull()
    }
}
