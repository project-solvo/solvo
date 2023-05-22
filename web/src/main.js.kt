package org.solvo.web

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.accounts.LoginSignUpContent
import org.solvo.web.accounts.RegisterLoginViewModel

fun main() {
    onWasmReady {
        Window("Solvo") {
            MainContent()
        }
    }
}

@Composable
private fun MainContent() {
    val model = remember { RegisterLoginViewModel() }
    // HomePageContent()
    LoginSignUpContent(model)
}

@Composable
fun HomePageContent() {
    MaterialTheme {
        var notifyMenu = false
        var personalMenu = false
        var settingMenu = false
        Column {
            TopAppBar(
                title = {
                    Text(text = "Solvo")
                },
                navigationIcon = {
                    IconButton(onClick = {/* Go to home page */ }) {
                        Icon(Icons.Filled.Home, null)
                    }
                },
                actions = {
                    IconButton(onClick = { notifyMenu = !notifyMenu }) {
                        Icon(Icons.Filled.Notifications, null)
                    }
                    IconButton(onClick = { personalMenu = !personalMenu }) {
                        Icon(Icons.Filled.Person, null)
                    }
                    IconButton(onClick = { settingMenu = !settingMenu }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                    DropdownMenu(
                        expanded = notifyMenu,
                        onDismissRequest = { notifyMenu = false },
                        offset = DpOffset(x = 10.dp, y = (-60).dp)
                    ) {
                        DropdownMenuItem(
                            text = {},
                            onClick = {},
                        )
                    }
//                    DropdownMenu(
//                        expanded = personalMenu,
//                        onDismissRequest = { personalMenu = false},
//                        offset = DpOffset(x = 10.dp, y = (-60).dp)
//                    ) {
//
//                    }
//                    DropdownMenu(
//                        expanded = settingMenu,
//                        onDismissRequest = { settingMenu = false},
//                        offset = DpOffset(x = 10.dp, y = (-60).dp)
//                    ) {
//
//                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}