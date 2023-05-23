package org.solvo.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Window
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Solvo") {
            MainContent()
        }
    }
}

@Composable
private fun MainContent() {
    HomePageContent()
}

@Composable
fun HomePageContent() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            createTopAppBar()
            Text(
                text = "Courses",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                modifier = Modifier.padding(25.dp),
            )
            val courses = mutableListOf<String>()
            for (i in 50001 until 50012) {
                courses.add(i.toString())
            }
            createCourses(courses)
        }
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
                // window.location.href = "register.html"
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
                    text = {},
                    onClick = {},
                )
            }
            DropdownMenu(
                expanded = settingMenu1,
                onDismissRequest = { settingMenu1 = false },
                offset = DpOffset(x = 10.dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = {},
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
private fun createCourses(courseNames: List<String>) {
    FlowRow(
        modifier = Modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (courseName in courseNames) {
            createButtonForCourse(courseName)
        }
    }
}

@Composable
private fun createButtonForCourse(courseName: String) {
    ElevatedCard(
        onClick = {},
        modifier = Modifier.padding(20.dp).height(160.dp).width(200.dp).clickable {},
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = courseName,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.padding(5.dp),
        )
    }
}