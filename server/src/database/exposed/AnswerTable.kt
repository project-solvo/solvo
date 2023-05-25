package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table

object AnswerTable: Table("Answers") {
    val coid = reference("COID", CommentedObjectTable).uniqueIndex()
    val question = reference("questionId", QuestionTable.coid)

    val upvote = uinteger("upvoteCount").default(0u)
    val downvote = uinteger("downvoteCount").default(0u)

    override val primaryKey = PrimaryKey(AnswerTable.coid)
}