package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.Answer
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AnswerTable
import java.util.*

interface AnswerDBFacade : CommentedObjectDBFacade<Answer> {
    suspend fun upvote(uid: UUID, answer: Answer): Boolean
    suspend fun downvote(uid: UUID, answer: Answer): Boolean
    suspend fun unVote(uid: UUID, answer: Answer): Boolean
}

class AnswerDBFacadeImpl : AnswerDBFacade {
    private val commentedObjectDB = CommentedObjectDBFacadeImpl<Answer>()
    private val questionDB = QuestionDBFacadeImpl()

    override suspend fun upvote(uid: UUID, answer: Answer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun downvote(uid: UUID, answer: Answer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unVote(uid: UUID, answer: Answer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !AnswerTable.select(AnswerTable.coid eq coid).empty()
    }

    override suspend fun post(content: Answer): UUID? = dbQuery {
        if (!questionDB.contains(content.question)) return@dbQuery null

        val coid = commentedObjectDB.post(content) ?: return@dbQuery null
        assert(AnswerTable.insert {
            it[AnswerTable.coid] = coid
            it[AnswerTable.question] = content.question
        }.resultedValues?.singleOrNull() != null)

        coid
    }

    override suspend fun modify(content: Answer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun view(coid: UUID): Answer? {
        TODO("Not yet implemented")
    }

    override suspend fun like(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}
