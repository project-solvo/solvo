package org.solvo.model.api.communication

import org.solvo.model.foundation.Uuid

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