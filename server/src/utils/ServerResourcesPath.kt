package org.solvo.server.utils

import java.util.*

interface ServerResourcesPath {
    fun databasePath(): String
    fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String
}

class ServerResourcesPathImpl : ServerResourcesPath {
    private val host: String = System.getProperty("user.dir")

    override fun databasePath(): String {
        return "$host/db"
    }

    override fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String {
        return "$host/resources/$purpose/$resourceId"
    }
}
