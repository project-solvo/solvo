package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object ArticleTable: Table("Articles") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val course = reference("courseId", CourseTable)
    val term = reference("termId", TermTable)

    val stars = uinteger("starsCount").default(0u)
    val views = uinteger("viewsCount").default(0u)

    override val primaryKey = PrimaryKey(ArticleTable.coid)
}