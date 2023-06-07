package org.solvo.server.database.control

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.communication.ReactionKind
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ReactionTable
import java.util.*

interface ReactionDBControl {
    suspend fun post(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean
    suspend fun getAllReactions(userId: UUID?, targetId: UUID): List<Reaction>
    suspend fun contains(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean
}

class ReactionDBControlImpl : ReactionDBControl {
    override suspend fun post(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean = dbQuery {
        ReactionTable.insertIgnore {
            it[user] = userId
            it[target] = targetId
            it[ReactionTable.reaction] = reaction
        }.resultedValues?.isNotEmpty() ?: false
    }

    override suspend fun getAllReactions(userId: UUID?, targetId: UUID): List<Reaction> = dbQuery {
        ReactionTable
            .select { ReactionTable.target eq targetId }
            .groupBy(ReactionTable.reaction)
            .map {
                val reaction = it[ReactionTable.reaction]
                Reaction(
                    kind = reaction,
                    count = it[ReactionTable.user.count()].toInt(),
                    self = userId?.let { contains(userId, targetId, reaction) } ?: false
                )
            }
    }

    override suspend fun contains(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean = dbQuery {
        !ReactionTable.select {
            (ReactionTable.user eq userId) and
                    (ReactionTable.target eq targetId) and
                    (ReactionTable.reaction eq reaction)
        }.empty()
    }
}