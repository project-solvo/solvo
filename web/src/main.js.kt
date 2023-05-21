package org.solvo.web

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var count by remember { mutableStateOf(0) }

            Text("Hello!")

            SuggestionChip({}, { Text("A Chip") })

            Button({ count++ }) {
                Text("You clicked $count times")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                var text by remember { mutableStateOf("") }
                OutlinedTextField(text, { text = it })
//
//                var showPopup by remember { mutableStateOf(false) }
//                if (showPopup) {
//                    Popup(onDismissRequest = { showPopup = false }) {
//                        Text("You clicked the button! Your text: $text")
//                    }
//                }
                IconButton(
                    {
                        if (text == "") {
                            window.alert("You clicked the button! No text was typed in the box. HAHA!")
                        } else {
                            window.alert("You clicked the button! Your text: $text")
                        }
                    },
                    Modifier.padding(32.dp),
                ) {
                    Image(Icons.Default.Check, "Check", Modifier.size(32.dp))
                }
            }
        }
    }
}

