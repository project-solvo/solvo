@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class Comment(
    val coid: Uuid,
    val author: User?,
    val content: String,
    val anonymity: Boolean,

    val pinned: Boolean,

    val parent: Uuid,
    val subComments: List<LightComment> = listOf(),
)


// shown as sub-comments below a parent comment.
@Immutable
@Serializable
class LightComment(
    val authorId: Uuid,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
)