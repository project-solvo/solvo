package org.solvo.server.database

import io.ktor.http.*
import org.solvo.server.ServerContext
import org.solvo.server.database.control.ResourcesDBControl
import org.solvo.server.utils.ServerPathType
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.io.InputStream
import java.util.*

interface ResourceDBFacade {
    suspend fun postImage(uid: UUID, input: InputStream, purpose: StaticResourcePurpose, contentType: ContentType): UUID
    suspend fun getImage(resourceId: UUID): File?
    suspend fun tryDeleteImage(resourceId: UUID): Boolean
    suspend fun getContentType(file: File): ContentType
}

class ResourceDBFacadeImpl(
    private val resources: ResourcesDBControl,
) : ResourceDBFacade {
    override suspend fun postImage(
        uid: UUID,
        input: InputStream,
        purpose: StaticResourcePurpose,
        contentType: ContentType
    ): UUID {
        val newImageId = resources.addResource(purpose, contentType)
        val path = ServerContext.paths.resolveResourcePath(newImageId, purpose, ServerPathType.LOCAL)

        ServerContext.files.write(input, path)
        return newImageId
    }

    override suspend fun getImage(resourceId: UUID): File? {
        val purpose = resources.getPurpose(resourceId) ?: return null

        val path = ServerContext.paths.resolveResourcePath(resourceId, purpose, ServerPathType.LOCAL)
        return File(path)
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