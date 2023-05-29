package org.solvo.server.utils

import java.util.*

interface ServerResourcesPath {
    fun databasePath(): String
    fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String
}

class ServerResourcesPathImpl : ServerResourcesPath {
    private val host: String = "http://localhost" // TODO

    override fun databasePath(): String {
        return "$host/db"
    }

    override fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String {
        return "$host/resource/$purpose/$resourceId"
    }
}
