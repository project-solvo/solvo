@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class QuestionUpstream(
    override val content: String,
    override val anonymity: Boolean,

    val code: String,
) : CommentableUpstream

@Immutable
@Serializable
class QuestionDownstream(
    override val coid: Uuid,
    override val author: User?,
    override val content: String,
    override val anonymity: Boolean,
    override val likes: UInt,

    val code: String,
    val article: Uuid,
    val answers: List<AnswerDownstream>,
    val comments: List<CommentDownstream>,
) : CommentableDownstream