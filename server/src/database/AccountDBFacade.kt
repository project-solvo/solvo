package org.solvo.server.database

import org.solvo.model.User
import org.solvo.model.api.AccountChecker
import org.solvo.model.api.AuthResponse
import org.solvo.model.api.AuthStatus
import org.solvo.server.ServerContext
import org.solvo.server.database.control.AccountDBControl
import org.solvo.server.database.control.ResourcesDBControl
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.io.InputStream
import java.util.*

interface AccountDBFacade {
    suspend fun register(username: String, hash: ByteArray): AuthResponse
    suspend fun getUsernameValidity(username: String): Boolean
    suspend fun login(username: String, hash: ByteArray): AuthResponse
    suspend fun getUserAvatar(uid: UUID): File?
    suspend fun uploadNewAvatar(uid: UUID, input: InputStream, contentDBFacade: ContentDBFacade): String
    suspend fun getUserInfo(uid: UUID): User?
}

class AccountDBFacadeImpl(
    private val accounts: AccountDBControl,
    private val resources: ResourcesDBControl,
) : AccountDBFacade {
    override suspend fun register(username: String, hash: ByteArray): AuthResponse {
        if (accounts.getId(username) != null) {
            return AuthResponse(AuthStatus.DUPLICATED_USERNAME)
        }

        val status = AccountChecker.checkUserNameValidity(username)
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

    override suspend fun getUsernameValidity(username: String): Boolean {
        return accounts.getId(username) == null
    }

    override suspend fun getUserAvatar(uid: UUID): File? {
        val resourceId = accounts.getAvatar(uid) ?: return null
        val path = ServerContext.paths.staticResourcePath(resourceId, StaticResourcePurpose.USER_AVATAR)
        return File(path)
    }

    override suspend fun getUserInfo(uid: UUID): User? {
        return accounts.getUserInfo(uid)
    }

    override suspend fun uploadNewAvatar(uid: UUID, input: InputStream, contentDBFacade: ContentDBFacade): String {
        val oldAvatarId = accounts.getAvatar(uid)
        val newAvatarId = contentDBFacade.postImage(uid, input, StaticResourcePurpose.USER_AVATAR)
        accounts.modifyAvatar(uid, newAvatarId)

        if (oldAvatarId != null) {
            if (contentDBFacade.tryDeleteImage(oldAvatarId)) {
                val path = ServerContext.paths.staticResourcePath(oldAvatarId, StaticResourcePurpose.USER_AVATAR)
                ServerContext.files.delete(path)
            }
        }

        return ServerContext.paths.staticResourcePath(newAvatarId, StaticResourcePurpose.USER_AVATAR)
    }
}