package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Immutable
@Serializable
class SharedContent(
    val content: String
) {
    companion object {
        val nullContent = SharedContent("")
    }
}