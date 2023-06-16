package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable

@Immutable
@Serializable
data class AdminSettings(
    val operators: List<User>,
) 