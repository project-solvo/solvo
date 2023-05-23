package org.solvo.model

import org.solvo.model.utils.Digest

object AccountChecker {
    const val USERNAME_MAX_LENGTH = 16
    const val HASH_SIZE = 32

    private val regex = Regex("^[a-zA-Z0-9_-]+$")

    fun checkUserNameValidity(username: String): AuthStatus {
        if (username.length > USERNAME_MAX_LENGTH) {
            return AuthStatus.USERNAME_TOO_LONG
        }
        val valid = regex.matches(username)
        if (!valid) {
            return AuthStatus.INVALID_USERNAME
        }
        return AuthStatus.SUCCESS
    }

    fun hashPassword(string: String): ByteArray {
        return Digest.md5(string.encodeToByteArray()).take(16).toByteArray()
    }
}