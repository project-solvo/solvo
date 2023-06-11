package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object AnswerCodeTable: Table() {
    val coid = reference("coid", CommentedObjectTable).uniqueIndex()
    val parent = reference("parentCOID", CommentedObjectTable)
    val code = integer("code").autoIncrement()

    init {
        uniqueIndex(parent, code)
    }

    override val primaryKey = PrimaryKey(coid)
}