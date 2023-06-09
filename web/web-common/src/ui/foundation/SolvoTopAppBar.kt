package org.solvo.web.ui.foundation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.solvo.web.document.History
import org.solvo.web.requests.client
import org.solvo.web.session.LocalUserViewModel
import org.solvo.web.session.UserViewModel
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.image.RoundedUserAvatar

@Composable
fun SolvoTopAppBar(
    userViewModel: UserViewModel = LocalUserViewModel.current,
    additionalNavigationIcons: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {}
) {
    var notifyMenu1 by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            title()
        },
        Modifier.fillMaxWidth(),
        navigationIcon = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
//                navigationIcon()
                IconButton(onClick = wrapClearFocus {
                    History.navigate { home() }
                }) {
                    Icon(Icons.Filled.Home, null)
                }
                additionalNavigationIcons()
            }
        },
        actions = {
            val windowState = LocalSolvoWindow.current
            IconButton(onClick = wrapClearFocus {
                windowState.setDarkMode(
                    when (windowState.preferDarkMode.value) {
                        false -> true
                        true -> false
                        null -> true
                    }
                )
                History.refresh()
                // temporarily fix compose bug
            }) {
                val isInDarkMode by windowState.preferDarkMode.collectAsState()
                Icon(
                    when (isInDarkMode) {
                        null -> Icons.Default.LightMode
                        false -> Icons.Default.LightMode
                        true -> Icons.Default.DarkMode
                    }, null
                )
            }
//            IconButton(onClick = wrapClearFocus { notifyMenu1 = !notifyMenu1 }) {
//                Icon(Icons.Filled.Notifications, null)
//            }

            UserIcons(userViewModel)

            DropdownMenu(
                expanded = notifyMenu1,
                onDismissRequest = { notifyMenu1 = false },
                offset = DpOffset(x = 10.dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = {},
                    onClick = wrapClearFocus {},
                )
            }
//            DropdownMenu(
//                expanded = personalMenu1,
//                onDismissRequest = { personalMenu1 = false },
//                offset = DpOffset(x = 10.dp, y = 0.dp)
//            ) {
//                DropdownMenuItem(
//                    text = { Text("Personal page") },
//                    onClick = {
//                        personalMenu1 = false
//                    },
//                )
//                DropdownMenuItem(
//                    text = { Text("Log out") },
//                    onClick = {
//                        History.navigate { auth() }
//                        personalMenu1 = false
//                    },
//                )
//            }
//            DropdownMenu(
//                expanded = settingMenu1,
//                onDismissRequest = { settingMenu1 = false },
//                offset = DpOffset(x = 10.dp, y = 0.dp)
//            ) {
//                DropdownMenuItem(
//                    text = { Text("Appearance") },
//                    onClick = {
//                        settingMenu1 = false
//                    },
//                )
//                DropdownMenuItem(
//                    text = { Text("Help") },
//                    onClick = {},
//                )
//            }
        },
    )
}

@Composable
private fun RowScope.UserIcons(
    userViewModel: UserViewModel,
) {
    val loggedIn by userViewModel.isLoggedIn.collectAsState()
    AnimatedVisibility(loggedIn == true) {
        // logged in, show avatar + name
        val currentUser by userViewModel.user.collectAsState()
//        var showDropdown by remember { mutableStateOf(false) }
        IconButton(wrapClearFocus {
            History.navigate { user() }
        }) {
            RoundedUserAvatar(currentUser?.avatarUrl, currentUser?.username?.str, 32.dp)
        }
//        DropdownMenu(showDropdown, { showDropdown = false }) {
//            DropdownMenuItem(
//                { Text("Upload Avatar") },
//                {},
//                leadingIcon = { Icon(Icons.Outlined.PersonOutline, null) }
//            )
//        }
    }
    AnimatedVisibility(loggedIn == true) {
        IconButton(onClick = wrapClearFocus {
            client.invalidateToken()
        }) {
            Icon(Icons.Filled.Logout, null)
        }
    }
//    AnimatedVisibility(loggedIn == true) {
//        IconButton(onClick = wrapClearFocus {}) {
//            Icon(Icons.Filled.Settings, null)
//        }
//    }
    AnimatedVisibility(loggedIn == null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = wrapClearFocus {
                History.navigate { auth() }
            }) {
                Icon(Icons.Filled.Person, null)
            }
            LinearProgressIndicator(Modifier.padding(end = 16.dp).width(32.dp).height(2.dp))
        }
    }

    AnimatedVisibility(loggedIn == false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalButton(
                onClick = wrapClearFocus {
                    if (loggedIn == false) {
                        History.navigate { auth() }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Login")
            }
            TextButton(onClick = wrapClearFocus {
                if (loggedIn == false) {
                    History.navigate { auth(isRegister = true) }
                }
            }) {
                Text("Register")
            }
        }
    }
}
