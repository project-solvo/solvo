package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object CommentTable: COIDTable("Comments") {
    val parent = reference("parentCOID", CommentedObjectTable)

    val asAnswer = bool("asAnswer").default(false)
    val pinned = bool("pinned").default(false)
}