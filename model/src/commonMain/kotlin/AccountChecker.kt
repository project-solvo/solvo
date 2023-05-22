package org.solvo.model

import org.solvo.model.utils.Digest

object AccountChecker {
    const val USERNAME_MAX_LENGTH = 16
    const val HASH_SIZE = 16

    private val regex = Regex("^[a-zA-Z0-9_-]+$")

    fun checkUserNameValidity(username: String): Reason {
        if (username.length > 16) {
            return Reason.USERNAME_TOO_LONG;
        }
        val valid = regex.matches(username);
        if (!valid) {
            return Reason.INVALID_USERNAME;
        }
        return Reason.VALID_USERNAME;
    }

    fun hashPassword(string: String): ByteArray {
        return Digest.md5(string.encodeToByteArray())
    }
}