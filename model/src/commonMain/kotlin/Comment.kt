@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer


@Immutable
@Serializable
class CommentUpstream(
    override val content: String,
    override val anonymity: Boolean,
) : CommentableUpstream

@Immutable
@Serializable
class CommentDownstream(
    override val coid: Uuid,
    override val author: User?,
    override val content: String,
    override val anonymity: Boolean,
    override val likes: UInt,
    override val dislikes: UInt,

    val parent: Uuid,
    val pinned: Boolean,
    val postTime: Long,
    val lastEditTime: Long,
    val lastCommentTime: Long,
    val previewSubComments: List<LightCommentDownstream>, // up to 3
    val allSubCommentIds: List<Uuid>,
) : CommentableDownstream

@Immutable
@Serializable
class LightCommentDownstream(
    val author: User?,
    val content: String,
)
