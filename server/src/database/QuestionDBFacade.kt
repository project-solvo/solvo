package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.solvo.model.Question
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface QuestionDBFacade: CommentedObjectDBFacade<Question> {
    suspend fun getId(articleId: UUID, index: String): UUID?
}

class QuestionDBFacadeImpl : QuestionDBFacade, CommentedObjectDBFacadeImpl<Question>() {
    override val associatedTable: Table = QuestionTable

    override suspend fun getId(articleId: UUID, index: String): UUID? = dbQuery {
        QuestionTable
            .select((QuestionTable.article eq articleId) and (QuestionTable.index eq index))
            .map { it[QuestionTable.coid].value }
            .singleOrNull()
    }

    override suspend fun post(content: Question): UUID? = dbQuery {
        if (content.index.length > DatabaseModel.QUESTION_INDEX_MAX_LENGTH) return@dbQuery null
        super.post(content)
    }

    override fun associateTableUpdates(it: InsertStatement<Number>, coid: UUID, content: Question, args: List<Any?>) {
        it[QuestionTable.coid] = coid
        it[QuestionTable.article] = content.article?.coid!!
        it[QuestionTable.index] = content.index
    }

    override suspend fun view(coid: UUID): Question? {
        TODO("Not yet implemented")
    }
}
