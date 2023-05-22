package org.solvo.model

import kotlinx.serialization.Serializable

@Serializable
class AuthRequest(
    val username: String,
    val password: String,
)

@Serializable
class AuthResponse(
    val status: AuthStatus,
    val token: String = "",
)

enum class AuthStatus{
    SUCCESS,
    INVALID_USERNAME,
    USERNAME_TOO_LONG,
    DUPLICATED_USERNAME,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
}

