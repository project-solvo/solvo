package org.solvo.model

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Immutable
@Serializable
class Question(
    val qid: String
)