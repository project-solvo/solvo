package org.solvo.web.accounts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.solvo.model.api.LiteralChecker
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.AuthStatus
import org.solvo.model.utils.ModelConstraints
import org.solvo.web.document.History
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.requests.client
import org.solvo.web.session.LocalSessionToken
import org.solvo.web.viewModel.AbstractViewModel


@Stable
private fun PathParameters.isRegister(): Flow<Boolean> {
    return argument(WebPagePathPatterns.VAR_AUTH_METHOD).map { authMethod ->
        authMethod == WebPagePathPatterns.VAR_AUTH_METHOD_REGISTER
    }
}

@Stable
class AuthenticationViewModel : AbstractViewModel() {
    private val pathParameters = PathParameters(WebPagePathPatterns.auth)
    val isRegister = pathParameters.isRegister().stateInBackground(false)

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


    val isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun setUsername(username: String) {
        flushErrors()
        _username.value = username.trim()
        val validity = LiteralChecker.checkUsername(username)
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

        doAuth(username, password, isRegister.value)
    }

    private suspend fun doAuth(username: String, password: String, isRegister: Boolean) {
        val response = client.accounts.authenticate(username, password, isRegister)
        when (response.status) {
            AuthStatus.SUCCESS -> {
                if (isRegister) {
                    History.pushState { auth(isRegister = false, recordRefer = false) }
                    // register OK, then log in
                    doAuth(username, password, isRegister = false)
                } else {
                    LocalSessionToken.value = response.token
                    History.navigate { authReturnOrHome() }
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
        History.pushState { auth(isRegister = !isRegister.value) }
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
        AuthStatus.USERNAME_TOO_LONG -> "Username is too long. Maximum length is ${ModelConstraints.USERNAME_MAX_LENGTH} characters"
        AuthStatus.DUPLICATED_USERNAME -> "Username is already taken. Please pick another one"
        AuthStatus.SUCCESS -> null
        AuthStatus.USER_NOT_FOUND -> "User not found"
        AuthStatus.WRONG_PASSWORD -> "Wrong password"
    }
}
