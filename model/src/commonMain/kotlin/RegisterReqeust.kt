package org.solvo.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReqeust(
    val username: String,
    val password: String,
)