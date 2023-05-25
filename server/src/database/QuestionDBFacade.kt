package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.Question
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface QuestionDBFacade {
    suspend fun contains(coid: UUID): Boolean
    suspend fun post(content: Question, articleId: UUID): UUID?
    suspend fun modify(content: Question): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): Question?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}

class QuestionDBFacadeImpl : QuestionDBFacade {
    private val commentedObjectDB = CommentedObjectDBFacadeImpl<Question>()

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !QuestionTable.select(QuestionTable.coid eq coid).empty()
    }

    override suspend fun post(content: Question, articleId: UUID): UUID? = dbQuery {
        val coid = commentedObjectDB.post(content) ?: return@dbQuery null

        assert(QuestionTable.insert {
            it[QuestionTable.coid] = coid
            it[QuestionTable.article] = articleId
        }.resultedValues != null)

        coid
    }

    override suspend fun modify(content: Question): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun view(coid: UUID): Question? {
        TODO("Not yet implemented")
    }

    override suspend fun like(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}
