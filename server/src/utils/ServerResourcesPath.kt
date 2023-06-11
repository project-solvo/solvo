package org.solvo.server.utils

import java.util.*

interface ServerResourcesPath {
    fun databasePath(): String
    fun resolveResourcePath(
        resourceId: UUID,
        purpose: StaticResourcePurpose,
    ): String
    fun resolveRelativeResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String
    fun resourcesPath(): String
    fun resolveResourceIdFromPath(path: String): UUID?
}

class ServerResourcesPathImpl : ServerResourcesPath {
    private val localRoot: String = System.getProperty("user.dir")

    override fun databasePath(): String {
        return "$localRoot/db"
    }

    override fun resourcesPath(): String {
        return "$localRoot/resources"
    }

    override fun resolveResourcePath(
        resourceId: UUID,
        purpose: StaticResourcePurpose,
    ): String {
        return localRoot + resolveRelativeResourcePath(resourceId, purpose)
    }

    override fun resolveRelativeResourcePath(
        resourceId: UUID,
        purpose: StaticResourcePurpose,
    ): String {
        return "/resources/$purpose/$resourceId"
    }

    override fun resolveResourceIdFromPath(path: String): UUID? {
        return try {
            UUID.fromString(path.takeLastWhile { it != '/' })
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
