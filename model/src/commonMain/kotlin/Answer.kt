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
    val coid: Uuid,
    val author: User?,
    val content: String,
    val anonymity: Boolean,

    val question: Uuid,
    val comments: List<Comment> = listOf(),
)