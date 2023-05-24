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
    val coid: Uuid?,
    val author: User?,
    val description: String,
    val anonymity: Boolean = false,

    val course: Course,
    val termYear: String,

    val questions: List<Question>,
    val comments: List<Comment> = listOf(),
) {
    // to pass compilation
    constructor(termYear: String, questions: List<Question>): this(
        coid = null,
        author = null,
        description = "",
        course = Course("", ""),
        termYear = termYear,
        questions = questions,
    )
}