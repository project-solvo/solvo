package org.solvo.server.utils

import java.nio.file.Path
import java.util.*

interface ServerResourcesPath {
    fun databasePath(): Path
    fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String
}

class ServerResourcesPathImpl : ServerResourcesPath {
    private val remote: String = "http://localhost"
    private val local: String = System.getProperty("user.dir")

    override fun databasePath(): Path {
        return Path.of("$local/db")
    }

    override fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String {
        return "$local/resources/$purpose/$resourceId"
    }
}

class ServerPaths(
    localPath: Path,
    remotePath: Path,
)
