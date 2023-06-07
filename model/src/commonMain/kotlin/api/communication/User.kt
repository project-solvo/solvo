package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class User(
    val id: @Serializable(UuidAsStringSerializer::class) Uuid,
    val username: String,
    val avatarUrl: String?,
)