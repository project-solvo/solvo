package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.api.communication.User
import org.solvo.model.utils.ModelConstraints
import org.solvo.model.utils.UserPermission
import org.solvo.server.ServerContext
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AuthTable
import org.solvo.server.database.exposed.UserTable
import org.solvo.server.utils.ServerPathType
import org.solvo.server.utils.StaticResourcePurpose
import java.util.*

interface AccountDBControl {
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
    suspend fun getUsername(uid: UUID): String?
    suspend fun getUserInfo(uid: UUID): User?
}

class AccountDBControlImpl : AccountDBControl {
    override suspend fun getId(username: String): UUID? = dbQuery {
        UserTable
            .select(UserTable.username eq username)
            .map { it[UserTable.id].value }
            .singleOrNull()
    }

    override suspend fun matchHash(uid: UUID, hash: ByteArray): Boolean = dbQuery {
        AuthTable
            .select(AuthTable.userId eq uid)
            .map { it[AuthTable.hash] }
            .singleOrNull()
    }.contentEquals(hash)

    override suspend fun addAccount(username: String, hash: ByteArray): UUID? = dbQuery {
        if (username.length > ModelConstraints.USERNAME_MAX_LENGTH) return@dbQuery null
        val userId = UserTable.insertIgnoreAndGetId {
            it[UserTable.username] = username
        }?.value
        if (userId != null) {
            AuthTable.insert {
                it[AuthTable.userId] = userId
                it[AuthTable.hash] = hash
            }
        }
        userId
    }
    override suspend fun modifyAvatar(uid: UUID, resourceId: UUID): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq uid }) {
            it[UserTable.avatar] = resourceId
        } > 0
    }

    override suspend fun modifyUsername(uid: UUID, username: String): Boolean = dbQuery {
        if (username.length > ModelConstraints.USERNAME_MAX_LENGTH) return@dbQuery false
        UserTable.update({ UserTable.id eq uid }) {
            it[UserTable.username] = username
        } > 0
    }

    override suspend fun modifyPassword(uid: UUID, hash: ByteArray): Boolean = dbQuery {
        AuthTable.update({ AuthTable.userId eq uid }) {
            it[AuthTable.hash] = hash
        } > 0
    }

    override suspend fun deleteAccount(uid: UUID): Boolean = dbQuery {
        if (UserTable.deleteWhere { UserTable.id eq uid } == 0) return@dbQuery false
        AuthTable.deleteWhere { AuthTable.userId eq uid } > 0
    }

    override suspend fun op(operatorId: UUID, uid: UUID): Boolean = dbQuery {
        val operatorPermission = getPermission(operatorId)
        val userPermission = getPermission(uid)

        if (operatorPermission == UserPermission.ROOT && userPermission == UserPermission.DEFAULT) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.permission] = UserPermission.OPERATOR
            } > 0
        } else false
    }

    override suspend fun deOp(operatorId: UUID, uid: UUID): Boolean = dbQuery {
        val operatorPermission = getPermission(operatorId)
        val userPermission = getPermission(uid)

        if (operatorPermission != UserPermission.ROOT && userPermission != UserPermission.OPERATOR) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.permission] = UserPermission.DEFAULT
            } > 0
        } else false
    }

    override suspend fun banUntil(operatorId: UUID, uid: UUID, time: Long): Boolean = dbQuery {
        val operatorPermission = getPermission(operatorId) ?: return@dbQuery false
        val userPermission = getPermission(uid) ?: return@dbQuery false

        if (operatorPermission > userPermission) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.bannedUntil] = time
            } > 0
        } else false
    }

    override suspend fun getPermission(uid: UUID): UserPermission? = dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.permission] }
            .singleOrNull()
    }

    override suspend fun getBannedUntil(uid: UUID): Long? = dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.bannedUntil] }
            .singleOrNull()
            ?.let { checkBanned(uid, it) }
    }

    override suspend fun isBanned(uid: UUID): Boolean = getBannedUntil(uid) != null

    private suspend fun checkBanned(uid: UUID, bannedUntil: Long): Long? = dbQuery {
        if (ServerContext.localtime.now() > bannedUntil) {
            UserTable.update({ UserTable.id eq uid }) {
                it[UserTable.bannedUntil] = null
            }
            null
        } else bannedUntil
    }

    override suspend fun getAvatar(uid: UUID): UUID? = dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.avatar]?.value }
            .singleOrNull()
    }

    override suspend fun getUsername(uid: UUID): String? = dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map { it[UserTable.username] }
            .singleOrNull()
    }

    override suspend fun getUserInfo(uid: UUID): User? = dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map {
                User(
                    uid,
                    it[UserTable.username],
                    it[UserTable.avatar]?.value?.let { avatarId ->
                        ServerContext.paths.resolveResourcePath(
                            avatarId,
                            StaticResourcePurpose.USER_AVATAR,
                            ServerPathType.REMOTE
                        )
                    }
                )
            }
            .singleOrNull()
    }
}
