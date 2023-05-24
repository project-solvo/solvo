package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext

object CourseTable: IntIdTable("Courses", "courseId") {
    val courseName = varchar("courseName", DatabaseModel.COURSE_NAME_MAX_LENGTH).uniqueIndex()
}

object TermTable: IntIdTable("Terms", "termId") {
    val termTime = varchar("termTime", DatabaseModel.TERM_TIME_MAX_LENGTH).uniqueIndex()
}

object ArticleTable: Table("Articles") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val course = reference("courseId", CourseTable)
    val term = reference("termId", TermTable)

    val stars = uinteger("starsCount").default(0u)
    val views = uinteger("viewsCount").default(0u)

    override val primaryKey = PrimaryKey(ArticleTable.coid)
}

object QuestionTable: Table("Questions") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val article = reference("articleId", ArticleTable.coid)

    override val primaryKey = PrimaryKey(QuestionTable.coid)
}

object AnswerTable: Table("Answers") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val question = reference("questionId", QuestionTable.coid)

    val upvote = uinteger("upvoteCount").default(0u)
    val downvote = uinteger("downvoteCount").default(0u)

    override val primaryKey = PrimaryKey(AnswerTable.coid)
}

object CommentTable: Table("Comments") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val parent = reference("parentCOID", CommentedObjectTable)

    val pinned = bool("pinned").default(false)

    override val primaryKey = PrimaryKey(coid)
}

object CommentedObjectTable: UUIDTable("CommentedObjects", "COID") {
    val author = reference("userId", UserTable)
    val content = largeText("content")

    val anonymity = bool("anonymity").default(false)
    val likes = uinteger("likesCount").default(0u)
    val comments = uinteger("commentsCount").default(0u)

    val postTime = long("postTime").default(ServerContext.localtime.now())
    val lastEditTime = long("lastEditTime").default(ServerContext.localtime.now())
    val lastCommentTime = long("lastCommentTime").default(ServerContext.localtime.now())
}