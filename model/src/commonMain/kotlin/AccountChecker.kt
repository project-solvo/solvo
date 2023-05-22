package org.solvo.model

object AccountChecker {
    private val regex = Regex("^[a-zA-Z0-9_-]+$");
    fun checkUserNameValidity(username: String) : Reason {
        if (username.length > 16) {
            return Reason.USERNAME_TOO_LONG;
        }
        val valid = regex.matches(username);
        if (!valid) {
            return Reason.INVALID_USERNAME;
        }
        return Reason.VALID_USERNAME;
    }

}