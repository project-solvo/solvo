package org.solvo.web.accounts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.model.AccountChecker
import org.solvo.model.Reason
import org.solvo.web.requests.client

@Stable
class RegisterLoginViewModel {
    private val _username: MutableState<String> = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password: MutableState<String> = mutableStateOf("")
    val password: State<String> get() = _password

    private val _verifyPassword: MutableState<String> = mutableStateOf("")
    val verifyPassword: State<String> get() = _verifyPassword

    val usernameError: MutableStateFlow<String?> = MutableStateFlow(null)
    val passwordError: MutableStateFlow<String?> = MutableStateFlow(null)
    val verifyPasswordError: MutableStateFlow<String?> = MutableStateFlow(null)

    val isRegister: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun setUsername(username: String) {
        _username.value = username.trim()
        val validity = AccountChecker.checkUserNameValidity(username)
        usernameError.value = validity.render()
    }

    fun setPassword(password: String) {
        _password.value = password
        passwordError.value = null
    }

    fun setVerifyPassword(password: String) {
        _verifyPassword.value = password
        verifyPasswordError.value = null
    }

    suspend fun onClickProceed() {
        if (!checkInputs()) return

        val username = username.value
        val password = password.value
        client.accounts.register(username, password)
    }

    private fun checkInputs(): Boolean {
        val username = username.value
        if (username.isEmpty()) {
            usernameError.value = "Please enter username"
            return false
        }
        val password = password.value
        if (password.isEmpty()) {
            passwordError.value = "Please enter password"
            return false
        }
        val verifyPassword = verifyPassword.value
        if (verifyPassword.isEmpty() && isRegister.value) {
            verifyPasswordError.value = "Please re-enter your password"
            return false
        }
        if (password != verifyPassword && isRegister.value) {
            verifyPasswordError.value = "Passwords do not match. Please re-enter your password"
            return false
        }
        return true
    }

    fun onClickSwitch() {
        isRegister.value = !isRegister.value
    }
}

private fun Reason.render(): String? {
    return when (this) {
        Reason.INVALID_USERNAME -> "Invalid username. Username must consist of English characters, digits, '-' or '_'"
        Reason.USERNAME_TOO_LONG -> "Username is too long. Maximum length is ${AccountChecker.USERNAME_MAX_LENGTH} characters"
        Reason.DUPLICATED_USERNAME -> "Username is already taken. Please pick another one"
        Reason.VALID_USERNAME -> null
    }
}
