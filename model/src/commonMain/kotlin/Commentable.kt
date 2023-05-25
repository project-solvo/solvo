package org.solvo.model

import org.solvo.model.foundation.Uuid

interface Commentable {
    var coid: Uuid?
    var author: User?
    val content: String
    val anonymity: Boolean
}