package org.solvo.model

import kotlinx.serialization.Serializable

@Serializable
class RegisterReqeust(
    val username: String,
    val password: ByteArray,
)

@Serializable
class RegisterResponse(
)

