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
    override var coid: Uuid? = null,
    override var author: User? = null,
    override val content: String,
    override val anonymity: Boolean = false,

    val index: String,
    val answers: List<Answer> = listOf(),
    val comments: List<Comment> = listOf(),
): Commentable {
    // to pass compilation
    constructor(content: String) : this(
        coid = null,
        author = null,
        content = content,
        index = "1a"
        )
}