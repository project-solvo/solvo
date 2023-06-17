package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.api.communication.User
import org.solvo.model.utils.ModelConstraints
import org.solvo.model.utils.NonBlankString
import org.solvo.model.utils.UserPermission
import org.solvo.server.ServerContext
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.AuthTable
import org.solvo.server.database.exposed.UserTable
import org.solvo.server.utils.StaticResourcePurpose
import java.util.*

interface AccountDBControl {
    suspend fun getId(displayName: String): UUID?

    suspend fun matchHash(uid: UUID, hash: ByteArray): Boolean
    suspend fun modifyUsername(uid: UUID, username: String): Boolean
    suspend fun modifyPassword(uid: UUID, hash: ByteArray): Boolean

    suspend fun addAccount(username: String, hash: ByteArray): UUID?
    suspend fun deleteAccount(uid: UUID): Boolean
    suspend fun modifyAvatar(uid: UUID, resourceId: UUID): Boolean
    suspend fun banUntil(operatorId: UUID, uid: UUID, time: Long): Boolean

    suspend fun getPermission(uid: UUID): UserPermission?
    suspend fun getBannedUntil(uid: UUID): Long?
    suspend fun isBanned(uid: UUID): Boolean
    suspend fun getAvatar(uid: UUID): UUID?
    suspend fun getUsername(uid: UUID): String?
    suspend fun getUserInfo(uid: UUID): User?
    suspend fun getOperators(): List<User>
    suspend fun setOperator(uid: UUID): Boolean
    suspend fun setRoot(uid: UUID): Boolean
    suspend fun removeOperator(uid: UUID): Boolean
    suspend fun searchUsers(username: String): List<User>
}

class AccountDBControlImpl : AccountDBControl {
    override suspend fun getId(displayName: String): UUID? = dbQuery {
        UserTable
            .select(UserTable.username eq displayName.lowercase())
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
            it[UserTable.username] = username.lowercase()
            it[UserTable.displayName] = username
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
            it[UserTable.username] = username.lowercase()
            it[UserTable.displayName] = username
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
            .map { it[UserTable.displayName] }
            .singleOrNull()
    }

    override suspend fun getUserInfo(uid: UUID): User? = dbQuery {
        UserTable
            .select(UserTable.id eq uid)
            .map {
                it.toUser()
            }
            .singleOrNull()
    }

    private fun ResultRow.toUser() = User(
        this[UserTable.id].value,
        NonBlankString.fromString(this[UserTable.displayName]),
        this[UserTable.avatar]?.value?.let { avatarId ->
            ServerContext.paths.resolveRelativeResourcePath(
                avatarId,
                StaticResourcePurpose.USER_AVATAR,
            )
        },
        this[UserTable.permission],
    )

    override suspend fun getOperators(): List<User> = dbQuery {
        UserTable.select(UserTable.permission eq UserPermission.OPERATOR).map { it.toUser() }
    }

    override suspend fun setOperator(uid: UUID): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq uid }, limit = 1) {
            it[permission] = UserPermission.OPERATOR
        } == 1
    }

    override suspend fun setRoot(uid: UUID): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq uid }, limit = 1) {
            it[permission] = UserPermission.ROOT
        } == 1
    }

    override suspend fun removeOperator(uid: UUID): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq uid }, limit = 1) {
            it[permission] = UserPermission.DEFAULT
        } == 1
    }

    override suspend fun searchUsers(username: String): List<User> = dbQuery {
        UserTable.select { UserTable.username regexp username.lowercase() }.map { it.toUser() }
    }
}
