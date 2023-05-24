package org.solvo.server.database

import org.solvo.model.Answer
import java.util.*

interface AnswerDBFacade: CommentedObjectDBFacade<Answer> {
    suspend fun upvote(uid: UUID, answer: Answer): Boolean
    suspend fun downvote(uid: UUID, answer: Answer): Boolean
    suspend fun unVote(uid: UUID, answer: Answer): Boolean
}