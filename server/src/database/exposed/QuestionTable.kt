package org.solvo.server.database.exposed

import org.solvo.model.utils.ModelConstraints

object QuestionTable: COIDTable("Questions") {
    val article = reference("articleId", ArticleTable.coid)
    val code = varchar("code", ModelConstraints.QUESTION_CODE_MAX_LENGTH)

    val sharedContent = reference("sharedContentId", SharedContentTable.id).nullable()

    init {
        uniqueIndex(article, code)
    }
}