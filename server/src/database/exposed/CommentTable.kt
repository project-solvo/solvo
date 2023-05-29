package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object CommentTable: Table("Comments") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val parent = reference("parentCOID", CommentedObjectTable)

    val asAnswer = bool("asAnswer").default(false)
    val pinned = bool("pinned").default(false)

    override val primaryKey = PrimaryKey(coid)
}