package org.solvo.model

import kotlinx.serialization.Serializable

@Serializable
class RegisterReqeust(
    val username: String,
    val password: ByteArray,
)

@Serializable
class RegisterResponse(
    var success: Boolean,
    var reason: Reason,
    )

@Serializable
class LoginResponse(
    var success: Boolean,
    var reason: Reason,
    var token: String,
)

enum class Reason{
    INVALID_USERNAME,
    USERNAME_TOO_LONG,
    DUPLICATED_USERNAME,
    VALID_USERNAME,
}

