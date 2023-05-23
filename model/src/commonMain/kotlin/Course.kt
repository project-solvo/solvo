package org.solvo.model

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Immutable
@Serializable
class Course(
    val code: String,
    val name: String,
)