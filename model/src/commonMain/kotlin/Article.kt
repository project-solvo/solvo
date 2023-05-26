@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class Article(
    override var coid: Uuid? = null,
    override var author: User? = null,
    override val content: String,
    override val anonymity: Boolean = false,

    val name: String,
    val course: Course,
    val termYear: String,

    val questions: List<Question>,
    val comments: List<Comment> = listOf(),
): Commentable {
    // to pass compilation
    constructor(termYear: String, questions: List<Question>): this(
        content = "",
        name = "",
        course = Course("", ""),
        termYear = termYear,
        questions = questions,
    )
}