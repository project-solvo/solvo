package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.DatabaseModel

object ArticleTable: Table("Articles") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val name = varchar("name", DatabaseModel.ARTICLE_NAME_MAX_LENGTH)
    val course = reference("courseId", CourseTable)
    val term = reference("termId", TermTable)

    val stars = uinteger("starsCount").default(0u)
    val views = uinteger("viewsCount").default(0u)

    init {
        uniqueIndex(name, course)
    }

    override val primaryKey = PrimaryKey(ArticleTable.coid)
}