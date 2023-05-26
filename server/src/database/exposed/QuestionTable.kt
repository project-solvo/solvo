package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.DatabaseModel

object QuestionTable: Table("Questions") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val article = reference("articleId", ArticleTable.coid)
    val index = varchar("index", DatabaseModel.QUESTION_INDEX_MAX_LENGTH)

    init {
        uniqueIndex(article, index)
    }

    override val primaryKey = PrimaryKey(QuestionTable.coid)
}