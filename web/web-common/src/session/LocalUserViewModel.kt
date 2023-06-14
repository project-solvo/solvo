package org.solvo.web.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import org.solvo.model.api.communication.User


val LocalUserViewModel = staticCompositionLocalOf<UserViewModel> {
    error("LocalUserViewModel not found")
}

@Composable
fun isLoggedIn(): Boolean {
    return LocalUserViewModel.current.isLoggedIn.collectAsState().value ?: false
}

val currentUser: User?
    @Composable
    get() {
        return LocalUserViewModel.current.user.collectAsState(null).value
    }