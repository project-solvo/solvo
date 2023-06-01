package org.solvo.server.utils

import java.util.*

interface ServerResourcesPath {
    fun databasePath(): String
    fun resolveResourcePath(
        resourceId: UUID,
        purpose: StaticResourcePurpose,
        pathType: ServerPathType = ServerPathType.REMOTE,
    ): String

    fun resourcesPath(): String
    fun resolveResourceIdFromPath(path: String): UUID?
}

class ServerResourcesPathImpl : ServerResourcesPath {
    private val remote: String = "http://localhost"
    private val local: String = System.getProperty("user.dir")

    override fun databasePath(): String {
        return "$local/db"
    }

    override fun resourcesPath(): String {
        return "$local/resources"
    }

    override fun resolveResourcePath(
        resourceId: UUID,
        purpose: StaticResourcePurpose,
        pathType: ServerPathType
    ): String {
        val base = when (pathType) {
            ServerPathType.LOCAL -> local
            ServerPathType.REMOTE -> remote
        }
        return "$base/resources/$purpose/$resourceId"
    }

    override fun resolveResourceIdFromPath(path: String): UUID? {
        return try {
            UUID.fromString(path.takeLastWhile { it != '/' })
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

enum class ServerPathType {
    LOCAL,
    REMOTE
}
