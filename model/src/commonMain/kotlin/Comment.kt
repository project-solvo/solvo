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
    val id: Uuid,
    val parent: Uuid?,
    val author: User,
    val subComments: List<LightComment>,
    val content: String,
)


// shown as sub-comments below a parent comment.
@Serializable
@Immutable
class LightComment(
    val authorId: Uuid,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
)