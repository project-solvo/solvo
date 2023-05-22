package org.solvo.model

import kotlinx.serialization.Serializable

@Serializable
class RegisterReqeust(
    val username: String,
    val password: ByteArray,
)

@Serializable
class RegisterResponse(
    val success: Boolean,
    val reason: Reason,
)

@Serializable
class LoginResponse(
    val success: Boolean,
    val reason: Reason,
    val token: String,
)

enum class Reason {
    INVALID_USERNAME,
    USERNAME_TOO_LONG,
    DUPLICATED_USERNAME,
    VALID_USERNAME,
}

