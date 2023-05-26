package org.solvo.web.accounts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.model.api.AccountChecker
import org.solvo.model.api.AuthStatus
import org.solvo.model.utils.DatabaseModel
import org.solvo.web.document.Cookies
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

@Stable
class AuthenticationViewModel : AbstractViewModel() {
    private val _username: MutableState<String> = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password: MutableState<String> = mutableStateOf("")
    val password: State<String> get() = _password

    private val _verifyPassword: MutableState<String> = mutableStateOf("")
    val verifyPassword: State<String> get() = _verifyPassword

    val usernameError: MutableStateFlow<String?> = MutableStateFlow(null)
    val usernameValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val passwordError: MutableStateFlow<String?> = MutableStateFlow(null)
    val verifyPasswordError: MutableStateFlow<String?> = MutableStateFlow(null)

    val isRegister: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun setUsername(username: String) {
        flushErrors()
        _username.value = username.trim()
        val validity = AccountChecker.checkUserNameValidity(username)
        usernameError.value = validity.render()
    }

    suspend fun checkUsernameValidity() {
        if (isRegister.value) {
            val response = client.accounts.checkUsername(_username.value)
            if (!response.validity) {
                usernameError.value = AuthStatus.DUPLICATED_USERNAME.render()
            } else {
                usernameValid.value = true
            }
        }
    }

    fun setPassword(password: String) {
        flushErrors()
        _password.value = password
        passwordError.value = null
    }

    fun setVerifyPassword(password: String) {
        flushErrors()
        _verifyPassword.value = password
        verifyPasswordError.value = null
    }

    suspend fun onClickProceed() {
        if (!checkInputs()) return

        val username = username.value
        val password = password.value

        val response = client.accounts.authenticate(username, password, isRegister.value)
        when (response.status) {
            AuthStatus.SUCCESS -> {
                if (isRegister.value) {
                    isRegister.value = false
                    onClickProceed()
                } else {
                    Cookies.setCookie("token", response.token)
                    window.location.href = window.location.origin
                }
            }

            AuthStatus.INVALID_USERNAME,
            AuthStatus.USERNAME_TOO_LONG,
            AuthStatus.DUPLICATED_USERNAME,
            AuthStatus.USER_NOT_FOUND -> {
                usernameError.value = response.status.render()
            }

            AuthStatus.WRONG_PASSWORD -> {
                passwordError.value = response.status.render()
            }
        }
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
        flush()
        if (isProcessing.value) return
        isRegister.value = !isRegister.value
    }

    private fun flush() {
        _username.value = ""
        _password.value = ""
        _verifyPassword.value = ""
        flushErrors()
    }

    private fun flushErrors() {
        usernameError.value = null
        passwordError.value = null
        usernameValid.value = false
    }
}

private fun AuthStatus.render(): String? {
    return when (this) {
        AuthStatus.INVALID_USERNAME -> "Must consist of English characters, digits, '-' or '_'"
        AuthStatus.USERNAME_TOO_LONG -> "Username is too long. Maximum length is ${DatabaseModel.USERNAME_MAX_LENGTH} characters"
        AuthStatus.DUPLICATED_USERNAME -> "Username is already taken. Please pick another one"
        AuthStatus.SUCCESS -> null
        AuthStatus.USER_NOT_FOUND -> "User not found"
        AuthStatus.WRONG_PASSWORD -> "Wrong password"
    }
}
