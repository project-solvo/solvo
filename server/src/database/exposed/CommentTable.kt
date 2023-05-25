package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object CommentTable: Table("Comments") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val parent = reference("parentCOID", CommentedObjectTable)

    val pinned = bool("pinned").default(false)

    override val primaryKey = PrimaryKey(coid)
}