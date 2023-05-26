package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.QuestionDownstream
import org.solvo.model.QuestionUpstream
import org.solvo.model.User
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface QuestionDBFacade : CommentedObjectDBFacade<QuestionUpstream> {
    suspend fun post(content: QuestionUpstream, author: User, articleId: UUID): UUID?
    suspend fun getId(articleId: UUID, index: String): UUID?
}

class QuestionDBFacadeImpl : QuestionDBFacade, CommentedObjectDBFacadeImpl<QuestionUpstream>() {
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

    override suspend fun view(coid: UUID): QuestionDownstream? {
        TODO("Not yet implemented")
    }
}
