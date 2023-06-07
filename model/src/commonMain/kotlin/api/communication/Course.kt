package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Immutable
@Serializable
class Course(
    val code: String,
    val name: String,
) {
    override fun toString(): String = "$code $name"
}