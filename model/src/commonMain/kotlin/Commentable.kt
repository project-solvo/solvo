package org.solvo.model

import org.solvo.model.foundation.Uuid

interface Commentable {
    val coid: Uuid?
    val author: User?
    val content: String
    val anonymity: Boolean
}

interface CommentableUpstream {
    val content: String
    val anonymity: Boolean
}

interface CommentableDownstream {
    val coid: Uuid
    val author: User?
    val content: String
    val anonymity: Boolean
    val likes: UInt
    val dislikes: UInt
}