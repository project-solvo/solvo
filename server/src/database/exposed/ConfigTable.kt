package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.IntIdTable

object ConfigTable: IntIdTable() {
    private const val CONFIG_STRING_SIZE = 64
    val config = varchar("config", CONFIG_STRING_SIZE)
}