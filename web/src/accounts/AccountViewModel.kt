package org.solvo.web.accounts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

sealed class RegisterLoginViewModel {
    val username: MutableState<String> = mutableStateOf("")
    val password: MutableState<String> = mutableStateOf("")

    val usernameError: MutableStateFlow<String> = MutableStateFlow("")
    val passwordError: MutableStateFlow<String> = MutableStateFlow("")
}

class RegisterViewModel : RegisterLoginViewModel() {
    val verifyPassword: MutableState<String> = mutableStateOf("")
    val verifyPasswordError: MutableStateFlow<String> = MutableStateFlow("")

    fun onClickRegister() {

    }

    fun onClickGotoLogin() {

    }
}

class LoginViewModel : RegisterLoginViewModel() {
    fun onClickLogin() {

    }

    fun onClickGotoLogin() {

    }
}