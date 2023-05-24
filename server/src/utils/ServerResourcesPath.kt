package org.solvo.server.utils

import java.util.*

object ServerResourcesPath {
    fun databasePath(): String {
        return "/db"
    }

    fun staticResourcePath(resourceId: UUID, purpose: StaticResourcePurpose): String {
        return "/resource/$purpose/$resourceId"
    }
}
