package org.solvo.model

import org.solvo.model.annotations.Immutable

@Immutable
class Reaction(
    val kind: ReactionKind,
    val count: Int,
    val self: Boolean,
)

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