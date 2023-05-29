@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class Answer(
    override val coid: Uuid? = null,
    override val author: User? = null,
    override val content: String,
    override val anonymity: Boolean = false,

    val question: Uuid,
    val comments: List<Comment> = listOf(),
): Commentable

@Immutable
@Serializable
class AnswerUpstream(
    override val content: String,
    override val anonymity: Boolean,

    val question: Uuid,
): CommentableUpstream

@Immutable
@Serializable
class AnswerDownstream(
    override val coid: Uuid,
    override val author: User?,
    override val content: String,
    override val anonymity: Boolean,
    override val likes: UInt,

    val question: Uuid,
    val comments: List<CommentDownstream>,
    val upVotes: UInt,
    val downVotes: UInt,
): CommentableDownstream