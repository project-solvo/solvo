package org.solvo.server.database.control

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.AnswerDownstream
import org.solvo.model.AnswerUpstream
import org.solvo.model.User
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AnswerTable
import org.solvo.server.database.exposed.CommentedObjectTable
import java.util.*

interface AnswerDBControl : CommentedObjectDBControl<AnswerUpstream> {
    suspend fun upvote(uid: UUID, coid: UUID): Boolean
    suspend fun downvote(uid: UUID, coid: UUID): Boolean
    suspend fun unVote(uid: UUID, coid: UUID): Boolean
    override suspend fun view(coid: UUID): AnswerDownstream?
}

class AnswerDBControlImpl(
    private val accountDB: AccountDBControl,
) : AnswerDBControl, CommentedObjectDBControlImpl<AnswerUpstream>() {
    override val associatedTable: Table = AnswerTable

    override suspend fun post(content: AnswerUpstream, author: User): UUID? = dbQuery {
        super.post(content, author)
    }

    override suspend fun associateTableUpdates(coid: UUID, content: AnswerUpstream, author: User): UUID = dbQuery {
        assert(AnswerTable.insert {
            it[AnswerTable.coid] = coid
            it[AnswerTable.question] = content.question
        }.resultedValues?.singleOrNull() != null)
        return@dbQuery coid
    }

    override suspend fun view(coid: UUID): AnswerDownstream? = dbQuery {
        AnswerTable
            .join(CommentedObjectTable, JoinType.INNER, AnswerTable.coid, CommentedObjectTable.id)
            .select(AnswerTable.coid eq coid)
            .map {
                AnswerDownstream(
                    coid = it[AnswerTable.coid].value,
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content],
                    anonymity = it[CommentedObjectTable.anonymity],
                    likes = it[CommentedObjectTable.likes],
                    question = it[AnswerTable.question].value,
                    comments = listOf(), // TODO
                    upVotes = it[AnswerTable.upvote],
                    downVotes = it[AnswerTable.downvote],
                )
            }.singleOrNull()
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
