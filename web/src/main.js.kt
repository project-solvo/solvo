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
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady
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
    HomePageContent()
    // LoginSignUpContent(model)
}

@Composable
fun HomePageContent() {
    MaterialTheme {
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
                    IconButton(onClick = {/* Show message bar */ }) {
                        Icon(Icons.Filled.Notifications, null)
                    }
                    IconButton(onClick = {/* Show user bar */ }) {
                        Icon(Icons.Filled.Person, null)
                    }
                    IconButton(onClick = {/* Show setting bar */ }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}