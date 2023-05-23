package org.solvo.server.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
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

    val stars = uinteger("starsCount")
    val views = uinteger("viewsCount")
}

object QuestionTable: UUIDTable("Questions", "QuestionId") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val article = reference("articleId", ArticleTable)
}

object AnswerTable: UUIDTable("Answers", "AnswerId") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val question = reference("questionId", AnswerTable)

    val upvote = uinteger("upvoteCount")
    val downvote = uinteger("downvoteCount")
}

object CommentTable: UUIDTable("") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val parent = reference("parentCOID", CommentedObjectTable)

    val pinned = bool("pinned")
}

object CommentedObjectTable: UUIDTable("CommentedObjects", "COID") {
    val author = reference("userId", AuthTable)

    val anonymity = bool("anonymity")
    val likes = uinteger("likesCount")
    val comments = uinteger("commentsCount")

    val postTime = long("postTime")
    val lastEditTime = long("lastEditTime")
    val lastCommentTime = long("lastCommentTime")
}