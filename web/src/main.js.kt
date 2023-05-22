package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Solvo") {
            LoginContent()
        }
    }
}

@Composable
private fun MainContent() {

}


@Composable
private fun LoginContent() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Text(
                "Solvo",
                modifier = Modifier.padding(bottom = 20.dp),
                fontSize = 30.sp,
                fontStyle = FontStyle.Normal,
            )

            Row(
                modifier = Modifier.padding(10.dp).offset(x = (-50).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Username", Modifier.padding(20.dp))
                OutlinedTextField(username, { username = it }, Modifier.height(48.dp), shape = RoundedCornerShape(8.dp))
            }

            Row(
                modifier = Modifier.padding(10.dp).offset(x = (-50).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Password", Modifier.padding(20.dp))
                OutlinedTextField(password, { password = it }, Modifier.height(48.dp), shape = RoundedCornerShape(8.dp))
            }

            Button(onClick = {}, modifier = Modifier.padding(10.dp), shape = RoundedCornerShape(8.dp)) {
                Text("Login")
            }

            ClickableText(
                text = buildAnnotatedString {
                    append("Does not have an account? Please ")
                    pushStyle(SpanStyle(color = Color.Blue))
                    append("sign up")
                    pop()
                },
                onClick = {},
            )

        }
    }
}