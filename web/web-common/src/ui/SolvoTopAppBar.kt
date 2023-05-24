package org.solvo.web.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.solvo.web.document.WindowState

@Composable
fun SolvoTopAppBar(
    windowState: WindowState,
    navigationIcon: @Composable () -> Unit = {}
) {
    var notifyMenu1 by remember { mutableStateOf(false) }
    var personalMenu1 by remember { mutableStateOf(false) }
    var settingMenu1 by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(text = "Solvo")
        },
        Modifier.fillMaxWidth(),
        navigationIcon = {
            Row {
                navigationIcon()
                IconButton(onClick = {
                    window.location.href = window.location.origin
                }) {
                    Icon(Icons.Filled.Home, null)
                }
            }
        },
        actions = {
            IconButton(onClick = {
                windowState.setDarkMode(
                    when (windowState.isInDarkMode.value) {
                        null -> false
                        false -> true
                        true -> null
                    }
                )
            }) {
                val isInDarkMode by windowState.isInDarkMode.collectAsState()
                Icon(
                    when (isInDarkMode) {
                        null -> Icons.Default.BrightnessAuto
                        false -> Icons.Default.LightMode
                        true -> Icons.Default.DarkMode
                    }, null
                )
            }
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
