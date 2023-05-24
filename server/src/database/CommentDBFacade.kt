package org.solvo.server.database

import org.solvo.model.Comment

interface CommentDBFacade: CommentedObjectDBFacade<Comment> {
    suspend fun pin(comment: Comment): Boolean
    suspend fun unpin(comment: Comment): Boolean
}