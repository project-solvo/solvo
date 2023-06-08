package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable
import org.solvo.model.utils.NonBlankString

@Immutable
@Serializable
class Course(
    val code: NonBlankString,
    val name: NonBlankString,
) {
    override fun toString(): String = "$code $name"

    companion object {
        fun fromString(code: String, name: String): Course = Course(
            NonBlankString.fromString(code),
            NonBlankString.fromString(name)
        )
    }
}