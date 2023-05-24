package org.solvo.model

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Immutable
@Serializable
class Article(
    val year: String,
    val questions: List<Question>,
)