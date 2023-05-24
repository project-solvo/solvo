@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class Question(
    val coid: Uuid?,
    val author: User?,
    val content: String,
    val anonymity: Boolean = false,

    val answers: List<Answer> = listOf(),
    val comments: List<Comment> = listOf(),
) {
    // to pass compilation
    constructor(content: String) : this(
        coid = null,
        author = null,
        content = content,
        )
}