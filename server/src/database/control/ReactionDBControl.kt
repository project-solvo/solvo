package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.communication.ReactionKind
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ReactionTable
import java.util.*

interface ReactionDBControl {
    suspend fun post(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean
    suspend fun delete(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean
    suspend fun getAllReactions(userId: UUID?, targetId: UUID): List<Reaction>
    suspend fun getUserIds(targetId: UUID, kind: ReactionKind): List<UUID>
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

    override suspend fun delete(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean = dbQuery {
        ReactionTable.deleteWhere {
            (ReactionTable.user eq userId) and
                    (ReactionTable.target eq targetId) and
                    (ReactionTable.reaction eq reaction)
        } > 0
    }

    override suspend fun getAllReactions(userId: UUID?, targetId: UUID): List<Reaction> = dbQuery {
        ReactionTable
            .slice(ReactionTable.user.count(), ReactionTable.target, ReactionTable.reaction)
            .select { ReactionTable.target eq targetId }
            .groupBy(ReactionTable.reaction)
            .map {
                val reaction = it[ReactionTable.reaction]
                Reaction(
                    kind = reaction,
                    count = it[ReactionTable.user.count()].toInt(),
                    isSelf = userId?.let { contains(userId, targetId, reaction) } ?: false
                )
            }
    }

    override suspend fun getUserIds(targetId: UUID, kind: ReactionKind): List<UUID> = dbQuery {
        ReactionTable
            .select { (ReactionTable.target eq targetId) and (ReactionTable.reaction eq kind) }
            .map { it[ReactionTable.user].value }
    }

    override suspend fun contains(userId: UUID, targetId: UUID, reaction: ReactionKind): Boolean = dbQuery {
        !ReactionTable.select {
            (ReactionTable.user eq userId) and
                    (ReactionTable.target eq targetId) and
                    (ReactionTable.reaction eq reaction)
        }.empty()
    }
}