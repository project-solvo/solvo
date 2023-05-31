package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.ModelConstraints

object ArticleTable: Table("Articles") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val code = varchar("code", ModelConstraints.ARTICLE_CODE_MAX_LENGTH)
    val displayName = varchar("displayName", ModelConstraints.ARTICLE_NAME_MAX_LENGTH)
    val course = reference("courseId", CourseTable)
    val term = reference("termId", TermTable)

    val stars = uinteger("starsCount").default(0u)
    val views = uinteger("viewsCount").default(0u)

    init {
        uniqueIndex(code, course)
    }

    override val primaryKey = PrimaryKey(ArticleTable.coid)
}