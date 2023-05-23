package org.solvo.model.api

import org.solvo.model.utils.DatabaseModel
import org.solvo.model.utils.Digest

object AccountChecker {
    private val regex = Regex("^[a-zA-Z0-9_-]+$")

    fun checkUserNameValidity(username: String): AuthStatus {
        if (username.length > DatabaseModel.USERNAME_MAX_LENGTH) {
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