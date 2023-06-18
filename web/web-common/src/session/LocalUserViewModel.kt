package org.solvo.web.session

import androidx.compose.runtime.*
import org.solvo.model.api.communication.User
import org.solvo.model.utils.UserPermission


val LocalUserViewModel = staticCompositionLocalOf<UserViewModel> {
    error("LocalUserViewModel not found")
}

@Composable
fun isLoggedIn(): Boolean {
    return LocalUserViewModel.current.isLoggedIn.collectAsState().value ?: false
}

@Composable
fun isLoggedInOrNull(): Boolean? {
    return LocalUserViewModel.current.isLoggedIn.collectAsState().value
}

val currentUser: User?
    @Composable
    get() {
        return LocalUserViewModel.current.user.collectAsState(null).value
    }

@Composable
fun currentUserHasPermission(permission: UserPermission): Boolean {
    val user by rememberUpdatedState(currentUser)
    return remember {
        derivedStateOf {
            user?.let { it.permission >= permission } ?: false
        }
    }.value
}
