package org.solvo.model.course

import kotlinx.serialization.Serializable

@Serializable
class Course(
    val code: String,
    val name: String,
)