package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.UUIDTable

object SharedContentTable: UUIDTable("SharedContents") {
    val content = largeText("content")
}