package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Serializable
@Immutable
data class Reaction(
    val kind: ReactionKind,
    val count: Int,
    val isSelf: Boolean,
)

@Serializable
enum class ReactionKind {
    PLUS_ONE,
    MINUS_ONE,
    SMILE,
    CELEBRATION,
    THINKING,
    HEART,
    ROCKET,
    EYES,
}