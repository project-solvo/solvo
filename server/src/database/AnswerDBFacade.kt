package org.solvo.server.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.solvo.model.AnswerDownstream
import org.solvo.model.AnswerUpstream
import org.solvo.model.User
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AnswerTable
import java.util.*

interface AnswerDBFacade : CommentedObjectDBFacade<AnswerUpstream> {
    suspend fun upvote(uid: UUID, coid: UUID): Boolean
    suspend fun downvote(uid: UUID, coid: UUID): Boolean
    suspend fun unVote(uid: UUID, coid: UUID): Boolean
}

class AnswerDBFacadeImpl(
    private val questionDB: QuestionDBFacade
) : AnswerDBFacade, CommentedObjectDBFacadeImpl<AnswerUpstream>() {
    override val associatedTable: Table = AnswerTable

    override suspend fun post(content: AnswerUpstream, author: User): UUID? = dbQuery {
        if (!questionDB.contains(content.question)) return@dbQuery null
        super.post(content, author)
    }

    override suspend fun associateTableUpdates(coid: UUID, content: AnswerUpstream, author: User): UUID = dbQuery {
        assert(AnswerTable.insert {
            it[AnswerTable.coid] = coid
            it[AnswerTable.question] = content.question
        }.resultedValues?.singleOrNull() != null)
        return@dbQuery coid
    }

    override suspend fun view(coid: UUID): AnswerDownstream? {
        TODO("Not yet implemented")
    }

    override suspend fun upvote(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun downvote(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unVote(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
