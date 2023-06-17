package org.solvo.server.database

import io.ktor.http.*
import org.solvo.model.api.LiteralChecker
import org.solvo.model.api.communication.AuthResponse
import org.solvo.model.api.communication.AuthStatus
import org.solvo.model.api.communication.User
import org.solvo.model.utils.UserPermission
import org.solvo.server.ServerContext
import org.solvo.server.database.control.AccountDBControl
import org.solvo.server.database.control.AuthTokenDBControl
import org.solvo.server.database.control.ResourcesDBControl
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.util.*

interface AccountDBFacade {
    suspend fun register(username: String, hash: ByteArray): AuthResponse
    suspend fun isUsernameNotTaken(username: String): Boolean
    suspend fun login(username: String, hash: ByteArray): AuthResponse
    suspend fun getUserAvatar(uid: UUID): Pair<File, ContentType>?
    suspend fun getUserInfo(uid: UUID): User?
    suspend fun searchUsers(username: String): List<User>
    suspend fun getOperators(): List<User>
    suspend fun setOperator(uid: UUID): Boolean
    suspend fun removeOperator(uid: UUID): Boolean
    suspend fun isOp(uid: UUID): Boolean
    suspend fun setRoot(uid: UUID): Boolean
    suspend fun addToken(uid: UUID, token: String): Boolean
    suspend fun matchToken(token: String): UUID?
    suspend fun removeAllTokens(uid: UUID): Boolean
}

class AccountDBFacadeImpl(
    private val accounts: AccountDBControl,
    private val tokens: AuthTokenDBControl,
    private val resources: ResourcesDBControl,
) : AccountDBFacade {
    override suspend fun register(username: String, hash: ByteArray): AuthResponse {
        if (accounts.getId(username) != null) {
            return AuthResponse(AuthStatus.DUPLICATED_USERNAME)
        }

        val status = LiteralChecker.checkUsername(username)
        if (status == AuthStatus.SUCCESS) {
            accounts.addAccount(username, hash)
        }
        return AuthResponse(status)
    }

    override suspend fun login(username: String, hash: ByteArray): AuthResponse {
        val id = accounts.getId(username) ?: return AuthResponse(AuthStatus.USER_NOT_FOUND)

        return if (accounts.matchHash(id, hash)) {
            AuthResponse(AuthStatus.SUCCESS, ServerContext.tokens.generateToken(id))
        } else {
            AuthResponse(AuthStatus.WRONG_PASSWORD)
        }
    }

    override suspend fun isUsernameNotTaken(username: String): Boolean {
        return accounts.getId(username) == null
    }

    override suspend fun getUserAvatar(uid: UUID): Pair<File, ContentType>? {
        val resourceId = accounts.getAvatar(uid) ?: return null
        val contentType = resources.getContentType(resourceId)
        val path = ServerContext.paths.resolveResourcePath(
            resourceId,
            StaticResourcePurpose.USER_AVATAR,
        )
        return Pair(File(path), contentType)
    }

    override suspend fun getUserInfo(uid: UUID): User? {
        return accounts.getUserInfo(uid)
    }

    override suspend fun searchUsers(username: String): List<User> {
        return accounts.searchUsers(username)
    }

    override suspend fun getOperators(): List<User> {
        return accounts.getOperators()
    }

    override suspend fun setOperator(uid: UUID): Boolean {
        return accounts.setOperator(uid)
    }

    override suspend fun removeOperator(uid: UUID): Boolean {
        return accounts.removeOperator(uid)
    }

    override suspend fun isOp(uid: UUID): Boolean {
        return accounts.getPermission(uid)?.let { it >= UserPermission.OPERATOR } == true
    }

    override suspend fun setRoot(uid: UUID): Boolean {
        return accounts.setRoot(uid)
    }

    override suspend fun addToken(uid: UUID, token: String): Boolean {
        return tokens.addToken(uid, token)
    }

    override suspend fun matchToken(token: String): UUID? {
        return tokens.matchToken(token)
    }

    override suspend fun removeAllTokens(uid: UUID): Boolean {
        return tokens.removeAllTokens(uid)
    }
}