package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.UUIDTable
import org.solvo.server.ServerContext

object TextTable: UUIDTable("Texts") {
    val content = largeText("content")
    val postTime = long("postTime").default(ServerContext.localtime.now())
}