package org.solvo.web.accounts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

class RegisterLoginViewModel {
    val username: MutableState<String> = mutableStateOf("")
    val password: MutableState<String> = mutableStateOf("")
    val verifyPassword: MutableState<String> = mutableStateOf("")

    val usernameError: MutableStateFlow<String?> = MutableStateFlow(null)
    val passwordError: MutableStateFlow<String?> = MutableStateFlow(null)
    val verifyPasswordError: MutableStateFlow<String?> = MutableStateFlow(null)

    val isRegister: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun onClickProceed() {

    }

    fun onClickSwitch() {
        isRegister.value = !isRegister.value
    }
}
