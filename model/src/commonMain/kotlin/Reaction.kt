package org.solvo.model

import org.solvo.model.annotations.Immutable

@Immutable
class Reaction(
    val kind: ReactionKind,
    val count: Int,
    val self: Boolean,
)

enum class ReactionKind(
    val id: Int,
) {
    PLUS_ONE(0),
    MINUS_ONE(1),
    SMILE(2),
    CELEBRATION(3),
    THINKING(4),
    HEART(5),
    ROCKET(6),
    EYES(7),
}