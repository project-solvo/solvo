package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.solvo.model.utils.UserPermission
import org.solvo.server.database.exposed.AuthTable
import org.solvo.server.database.exposed.UserTable
import org.solvo.server.utils.ServerLocalTime
import java.util.*

interface AccountDBFacade {
    suspend fun getId(username: String): UUID?

    suspend fun matchHash(uid: UUID, hash: ByteArray): Boolean
    suspend fun modifyUsername(uid: UUID, username: String): Boolean
    suspend fun modifyPassword(uid: UUID, hash: ByteArray): Boolean

    suspend fun addAccount(username: String, hash: ByteArray): UUID?
    suspend fun deleteAccount(uid: UUID): Boolean
    suspend fun modifyAvatar(uid: UUID, resourceId: UUID): Boolean
    suspend fun op(operatorId: UUID, uid: UUID): Boolean
    suspend fun deOp(operatorId: UUID, uid: UUID): Boolean
    suspend fun banUntil(operatorId: UUID, uid: UUID, time: Long): Boolean

    suspend fun getPermission(uid: UUID): UserPermission?
    suspend fun getBannedUntil(uid: UUID): Long?
    suspend fun isBanned(uid: UUID): Boolean
    suspend fun getAvatar(uid: UUID): UUID?
}

class AccountDBFacadeImpl : AccountDBFacade {
    override suspend fun getId(username: String): UUID? = DatabaseFactory.dbQuery {
        UserTable
            .select(UserTable.username eq username)
            .map { it[UserTable.id].value }
            .singleOrNull()
    }

    override suspend fun matchHash(uid: UUID, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable
            .select(AuthTable.userId eq uid)
            .map { it[AuthTable.hash] }
            .singleOrNull()
    }.contentEquals(hash)

    override suspend fun addAccount(username: String, hash: ByteArray): UUID? = DatabaseFactory.dbQuery {
        val userId = UserTable.insert {
            it[UserTable.username] = username
        }.resultedValues?.singleOrNull()?.let { it[UserTable.id].value }
        if (userId != null) {
            AuthTable.insert {
                it[AuthTable.userId] = userId
                it[AuthTable.hash] = hash
            }
        }
        userId
    }

    override suspend fun modifyAvatar(uid: UUID, resourceId: UUID): Boolean = DatabaseFactory.dbQuery {
        UserTable.update({ UserTable.id eq uid }) {
            it[UserTable.avatar] = resourceId
        } > 0
    }

    override suspend fun modifyUsername(uid: UUID, username: String): Boolean = DatabaseFactory.dbQuery {
        UserTable.update({ UserTable.id eq uid }) {
            it[UserTable.username] = username
        } > 0
    }

    override suspend fun modifyPassword(uid: UUID, hash: ByteArray): Boolean = DatabaseFactory.dbQuery {
        AuthTable.update({ AuthTable.userId eq uid }) {
            it[AuthTable.hash] = hash
        } > 0
    }

    override suspend fun deleteAccount(uid: UUID): Boolean = DatabaseFactory.dbQuery {
        if (UserTable.deleteWhere { UserTable.id eq uid } == 0) return@dbQuery false
        AuthTable.deleteWhere { AuthTable.userId eq uid } > 0
    }

    override suspend fun op(operatorId: UUID, uid: UUID): Boolean = DatabaseFactory.dbQuery {
        val operatorPermission = getPermission(operatorId)
        val userPermission = getPermission(uid)

        if (operatorPermission == UserPermission.ROOT && userPermission == UserPermission.DEFAULT) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.permission] = UserPermission.OPERATOR
            } > 0
        } else false
    }

    override suspend fun deOp(operatorId: UUID, uid: UUID): Boolean = DatabaseFactory.dbQuery {
        val operatorPermission = getPermission(operatorId)
        val userPermission = getPermission(uid)

        if (operatorPermission != UserPermission.ROOT && userPermission != UserPermission.OPERATOR) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.permission] = UserPermission.DEFAULT
            } > 0
        } else false
    }

    override suspend fun banUntil(operatorId: UUID, uid: UUID, time: Long): Boolean = DatabaseFactory.dbQuery {
        val operatorPermission = getPermission(operatorId) ?: return@dbQuery false
        val userPermission = getPermission(uid) ?: return@dbQuery false

        if (operatorPermission > userPermission) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.bannedUntil] = time
            } > 0
        } else false
    }

    override suspend fun getPermission(uid: UUID): UserPermission? = DatabaseFactory.dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.permission] }
            .singleOrNull()
    }

    override suspend fun getBannedUntil(uid: UUID): Long? = DatabaseFactory.dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.bannedUntil] }
            .singleOrNull()
            ?.let { checkBanned(uid, it) }
    }

    override suspend fun isBanned(uid: UUID): Boolean = getBannedUntil(uid) != null

    private suspend fun checkBanned(uid: UUID, bannedUntil: Long): Long? = DatabaseFactory.dbQuery {
        if (ServerLocalTime.now() > bannedUntil) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.bannedUntil] = null
            }
            null
        } else bannedUntil
    }

    override suspend fun getAvatar(uid: UUID): UUID? = DatabaseFactory.dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.avatar]?.value }
            .singleOrNull()
    }
}