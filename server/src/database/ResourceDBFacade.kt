package org.solvo.server.database

import io.ktor.http.*
import org.solvo.server.ServerContext
import org.solvo.server.database.control.AccountDBControl
import org.solvo.server.database.control.ResourcesDBControl
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.io.InputStream
import java.util.*

interface ResourceDBFacade {
    suspend fun postImage(uid: UUID, input: InputStream, purpose: StaticResourcePurpose, contentType: ContentType): UUID
    suspend fun getImage(resourceId: UUID): File?
    suspend fun tryDeleteImage(resourceId: UUID): Boolean
    suspend fun getContentType(file: File): ContentType
    suspend fun uploadNewAvatar(uid: UUID, input: InputStream, contentType: ContentType): String
    suspend fun changeAvatar(uid: UUID, iamgeId: UUID, contentType: ContentType): String
}

class ResourceDBFacadeImpl(
    private val accounts: AccountDBControl,
    private val resources: ResourcesDBControl,
) : ResourceDBFacade {
    override suspend fun postImage(
        uid: UUID,
        input: InputStream,
        purpose: StaticResourcePurpose,
        contentType: ContentType
    ): UUID {
        val newImageId = resources.addResource(purpose, contentType)
        val path = ServerContext.paths.resolveResourcePath(newImageId, purpose)

        ServerContext.files.write(input, path)
        return newImageId
    }

    override suspend fun getImage(resourceId: UUID): File? {
        val purpose = resources.getPurpose(resourceId) ?: return null

        val path = ServerContext.paths.resolveResourcePath(resourceId, purpose)
        return File(path)
    }

    override suspend fun uploadNewAvatar(
        uid: UUID,
        input: InputStream,
        contentType: ContentType,
    ): String {
        val newAvatarId = postImage(uid, input, StaticResourcePurpose.USER_AVATAR, contentType)
        return changeAvatar(uid, newAvatarId, contentType)
    }

    override suspend fun changeAvatar(
        uid: UUID,
        iamgeId: UUID,
        contentType: ContentType,
    ): String {
        val oldAvatarId = accounts.getAvatar(uid)
        accounts.modifyAvatar(uid, iamgeId)

        if (oldAvatarId != null) {
            if (tryDeleteImage(oldAvatarId)) {
                val path = ServerContext.paths.resolveResourcePath(
                    oldAvatarId,
                    StaticResourcePurpose.USER_AVATAR,
                )
                ServerContext.files.delete(path)
            }
        }

        return ServerContext.paths.resolveRelativeResourcePath(
            iamgeId,
            StaticResourcePurpose.USER_AVATAR,
        )
    }

    override suspend fun tryDeleteImage(resourceId: UUID): Boolean {
        return resources.tryDeleteResource(resourceId)
    }

    override suspend fun getContentType(file: File): ContentType {
        val resourceId = ServerContext.paths.resolveResourceIdFromPath(file.canonicalPath)
            ?: return ContentType.Any
        return resources.getContentType(resourceId)
    }
}