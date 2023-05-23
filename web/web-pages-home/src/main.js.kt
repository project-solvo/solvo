package org.solvo.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.document.SolvoWindow

fun main() {
    onWasmReady {
        SolvoWindow {
            HomePageContent()
        }
    }
}

@Composable
fun HomePageContent() {
    createTopAppBar()
    Column(
        modifier = Modifier.fillMaxSize().padding(100.dp).verticalScroll(rememberScrollState())
    ) {
        // Course title
        Text(
            text = "Courses",
            modifier = Modifier.padding(50.dp),
            style = MaterialTheme.typography.headlineLarge,
        )
        val courses = mutableListOf<String>()
        for (i in 50001..<50012) {
            courses.add(i.toString())
        }
        createFlowRow(courses)
    }
}

@Composable
private fun createTopAppBar() {
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

@Composable
private fun createFlowRow(items: List<String>) {
    FlowRow(
        modifier = Modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (courseName in items) {
            createElevatedCard(courseName)
        }
    }
}

@Composable
private fun createElevatedCard(item: String) {
    ElevatedCard(
        onClick = {},
        modifier = Modifier.padding(25.dp).height(200.dp).width(350.dp).clickable {},
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = item,
            modifier = Modifier.padding(15.dp),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
