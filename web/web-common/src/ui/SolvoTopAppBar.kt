package org.solvo.web.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.browser.window

@Composable
fun SolvoTopAppBar() {
    var notifyMenu1 by remember { mutableStateOf(false) }
    var personalMenu1 by remember { mutableStateOf(false) }
    var settingMenu1 by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(text = "Solvo")
        },
        Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = {/* Go to home page */ }) {
                Icon(Icons.Filled.Home, null)
            }
        },
        actions = {
            IconButton(onClick = { notifyMenu1 = true }) {
                Icon(Icons.Filled.Notifications, null)
            }
            IconButton(onClick = {
                personalMenu1 = true
            }) {
                Icon(Icons.Filled.Person, null)
            }
            IconButton(onClick = { settingMenu1 = true }) {
                Icon(Icons.Filled.Settings, null)
            }
            DropdownMenu(
                expanded = notifyMenu1,
                onDismissRequest = { notifyMenu1 = false },
                offset = DpOffset(x = 10.dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = {},
                    onClick = {},
                )
            }
            DropdownMenu(
                expanded = personalMenu1,
                onDismissRequest = { personalMenu1 = false },
                offset = DpOffset(x = 10.dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Personal page") },
                    onClick = {
                        personalMenu1 = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Log out") },
                    onClick = {
                        window.location.href = "register.html"
                        personalMenu1 = false
                    },
                )
            }
            DropdownMenu(
                expanded = settingMenu1,
                onDismissRequest = { settingMenu1 = false },
                offset = DpOffset(x = 10.dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Appearance") },
                    onClick = {
                        personalMenu1 = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Help") },
                    onClick = {},
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}
