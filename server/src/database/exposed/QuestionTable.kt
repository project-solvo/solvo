package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object QuestionTable: Table("Questions") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val article = reference("articleId", ArticleTable.coid)

    override val primaryKey = PrimaryKey(QuestionTable.coid)
}