package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.AnswerDownstream
import org.solvo.model.QuestionDownstream
import org.solvo.model.QuestionUpstream
import org.solvo.model.User
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AnswerTable
import org.solvo.server.database.exposed.CommentedObjectTable
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface QuestionDBControl : CommentedObjectDBControl<QuestionUpstream> {
    suspend fun post(content: QuestionUpstream, author: User, articleId: UUID): UUID?
    suspend fun getId(articleId: UUID, index: String): UUID?
    override suspend fun view(coid: UUID): QuestionDownstream?
}

class QuestionDBControlImpl(
    private val answerDB: AnswerDBControl,
    private val accountDB: AccountDBControl,
) : QuestionDBControl, CommentedObjectDBControlImpl<QuestionUpstream>() {
    override val associatedTable: Table = QuestionTable

    override suspend fun getId(articleId: UUID, index: String): UUID? = dbQuery {
        QuestionTable
            .select((QuestionTable.article eq articleId) and (QuestionTable.index eq index))
            .map { it[QuestionTable.coid].value }
            .singleOrNull()
    }

    @Deprecated("not supported", ReplaceWith("post(content, author, articleId)"))
    override suspend fun post(content: QuestionUpstream, author: User): UUID =
        error("Directly posting a question not supported")

    override suspend fun post(content: QuestionUpstream, author: User, articleId: UUID): UUID? = dbQuery {
            if (content.index.length > DatabaseModel.QUESTION_INDEX_MAX_LENGTH) return@dbQuery null
            val coid = super.post(content, author) ?: return@dbQuery null
            assert(QuestionTable.insert {
                it[QuestionTable.coid] = coid
                it[QuestionTable.article] = articleId
                it[QuestionTable.index] = content.index
            }.resultedValues?.singleOrNull() != null)
            coid
        }

    override suspend fun associateTableUpdates(coid: UUID, content: QuestionUpstream, author: User): UUID = coid

    override suspend fun view(coid: UUID): QuestionDownstream? = dbQuery {
        val answers: List<AnswerDownstream> = QuestionTable
            .join(AnswerTable, JoinType.INNER, QuestionTable.coid, AnswerTable.question)
            .select(QuestionTable.coid eq coid)
            .mapNotNull { answerDB.view(it[AnswerTable.coid].value) }

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
                    index = it[QuestionTable.index],
                    article = it[QuestionTable.article].value,
                    answers = answers,
                    comments = listOf(), // TODO
                )
            }.singleOrNull()
    }
}
