package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer
import org.solvo.model.utils.NonBlankString

@Immutable
@Serializable
data class User(
    val id: @Serializable(UuidAsStringSerializer::class) Uuid,
    val username: NonBlankString,
    val avatarUrl: String?,
)