package org.solvo.model.api.communication

import org.solvo.model.foundation.Uuid
import org.solvo.model.utils.NonBlankString

interface CommentableUpstream {
    val content: NonBlankString
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