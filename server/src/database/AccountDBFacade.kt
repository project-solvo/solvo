package org.solvo.server.database

import io.ktor.http.*
import org.solvo.model.User
import org.solvo.model.api.AccountChecker
import org.solvo.model.api.AuthResponse
import org.solvo.model.api.AuthStatus
import org.solvo.model.utils.UserPermission
import org.solvo.server.ServerContext
import org.solvo.server.database.control.AccountDBControl
import org.solvo.server.database.control.ResourcesDBControl
import org.solvo.server.utils.ServerPathType
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.io.InputStream
import java.util.*

interface AccountDBFacade {
    suspend fun register(username: String, hash: ByteArray): AuthResponse
    suspend fun getUsernameValidity(username: String): Boolean
    suspend fun login(username: String, hash: ByteArray): AuthResponse
    suspend fun getUserAvatar(uid: UUID): Pair<File, ContentType>?
    suspend fun uploadNewAvatar(
        uid: UUID,
        input: InputStream,
        contentType: ContentType,
        resourceDBFacade: ResourceDBFacade
    ): String

    suspend fun getUserInfo(uid: UUID): User?
    suspend fun isOp(uid: UUID): Boolean
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

    override suspend fun getUserAvatar(uid: UUID): Pair<File, ContentType>? {
        val resourceId = accounts.getAvatar(uid) ?: return null
        val contentType = resources.getContentType(resourceId)
        val path = ServerContext.paths.resolveResourcePath(
            resourceId,
            StaticResourcePurpose.USER_AVATAR,
            ServerPathType.LOCAL
        )
        return Pair(File(path), contentType)
    }

    override suspend fun getUserInfo(uid: UUID): User? {
        return accounts.getUserInfo(uid)
    }

    override suspend fun uploadNewAvatar(
        uid: UUID,
        input: InputStream,
        contentType: ContentType,
        resourceDBFacade: ResourceDBFacade
    ): String {
        val oldAvatarId = accounts.getAvatar(uid)
        val newAvatarId = resourceDBFacade.postImage(uid, input, StaticResourcePurpose.USER_AVATAR, contentType)
        accounts.modifyAvatar(uid, newAvatarId)

        if (oldAvatarId != null) {
            if (resourceDBFacade.tryDeleteImage(oldAvatarId)) {
                val path = ServerContext.paths.resolveResourcePath(
                    oldAvatarId,
                    StaticResourcePurpose.USER_AVATAR,
                    ServerPathType.LOCAL
                )
                ServerContext.files.delete(path)
            }
        }

        return ServerContext.paths.resolveResourcePath(
            newAvatarId,
            StaticResourcePurpose.USER_AVATAR,
            ServerPathType.REMOTE
        )
    }

    override suspend fun isOp(uid: UUID): Boolean {
        return accounts.getPermission(uid)?.let { it >= UserPermission.OPERATOR } == true
    }
}