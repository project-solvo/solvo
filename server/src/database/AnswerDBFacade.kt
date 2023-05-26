package org.solvo.server.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.solvo.model.Answer
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AnswerTable
import java.util.*

interface AnswerDBFacade : CommentedObjectDBFacade<Answer> {
    suspend fun upvote(uid: UUID, answer: Answer): Boolean
    suspend fun downvote(uid: UUID, answer: Answer): Boolean
    suspend fun unVote(uid: UUID, answer: Answer): Boolean
}

class AnswerDBFacadeImpl(
    private val questionDB: QuestionDBFacade
) : AnswerDBFacade, CommentedObjectDBFacadeImpl<Answer>() {
    override val associatedTable: Table = AnswerTable

    override suspend fun post(content: Answer): UUID? = dbQuery {
        if (!questionDB.contains(content.question)) return@dbQuery null
        super.post(content)
    }

    override fun associateTableUpdates(it: InsertStatement<Number>, coid: UUID, content: Answer, args: List<Any?>) {
        it[AnswerTable.coid] = coid
        it[AnswerTable.question] = content.question
    }

    override suspend fun view(coid: UUID): Answer? {
        TODO("Not yet implemented")
    }

    override suspend fun upvote(uid: UUID, answer: Answer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun downvote(uid: UUID, answer: Answer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unVote(uid: UUID, answer: Answer): Boolean {
        TODO("Not yet implemented")
    }
}
