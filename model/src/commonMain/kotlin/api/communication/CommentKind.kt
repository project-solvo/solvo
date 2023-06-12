package org.solvo.model.api.communication

import kotlinx.serialization.Serializable

@Serializable
enum class CommentKind {
    COMMENT,
    ANSWER,
    THOUGHT;
}

fun CommentKind.isAnswerOrThought(): Boolean {
    return this == CommentKind.ANSWER || this == CommentKind.THOUGHT
}