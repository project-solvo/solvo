package org.solvo.server.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.solvo.model.utils.DatabaseModel

object CourseTable: IntIdTable("Courses", "courseId") {
    val courseName = varchar("courseName", DatabaseModel.COURSE_NAME_MAX_LENGTH).uniqueIndex()
}

object TermTable: IntIdTable("Terms", "termId") {
    val termTime = varchar("termTime", DatabaseModel.TERM_TIME_MAX_LENGTH).uniqueIndex()
}

object ArticleTable: UUIDTable("Articles", "ArticleId") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val course = reference("courseId", CourseTable)
    val term = reference("termId", TermTable)

    val stars = uinteger("starsCount").default(0u)
    val views = uinteger("viewsCount").default(0u)
}

object QuestionTable: UUIDTable("Questions", "QuestionId") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val article = reference("articleId", ArticleTable)
}

object AnswerTable: UUIDTable("Answers", "AnswerId") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val question = reference("questionId", AnswerTable)

    val upvote = uinteger("upvoteCount").default(0u)
    val downvote = uinteger("downvoteCount").default(0u)
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

    val postTime = long("postTime")
    val lastEditTime = long("lastEditTime")
    val lastCommentTime = long("lastCommentTime")
}