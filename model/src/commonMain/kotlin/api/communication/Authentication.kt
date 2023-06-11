package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import org.solvo.model.utils.NonBlankString

@Serializable
class AuthRequest(
    val username: NonBlankString,
    val password: NonBlankString,
)

@Serializable
class AuthResponse(
    val status: AuthStatus,
    val token: String? = null,
)

@Serializable
class UsernameValidityResponse(
    val validity: Boolean
)

enum class AuthStatus{
    SUCCESS,
    INVALID_USERNAME,
    USERNAME_TOO_LONG,
    DUPLICATED_USERNAME,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
}

@Serializable
class ImageUrlExchange(
    val url: String,
)
