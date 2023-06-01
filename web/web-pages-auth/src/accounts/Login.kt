package org.solvo.web.accounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.web.viewModel.launchInBackground

@Composable
fun LoginSignUpContent(viewModel: AuthenticationViewModel) {
    val errorFontSize = 14.sp
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        val isRegister by viewModel.isRegister.collectAsState()
        val usernameError by viewModel.usernameError.collectAsState()
        val passwordError by viewModel.passwordError.collectAsState()
        val verifyPasswordError by viewModel.verifyPasswordError.collectAsState()
        Text(
            "Solvo",
            modifier = Modifier.padding(bottom = 20.dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.W800,
            fontStyle = FontStyle.Normal,
        )

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.setUsername(it) },
                isError = (usernameError != null),
                label = { Text("Username") },
                shape = RoundedCornerShape(8.dp)
            )
        }
        AnimatedVisibility(usernameError != null) {
            usernameError?.let {
                Text(
                    text = it,
                    fontSize = errorFontSize,
                    color = Color.Red,
                )
            }
        }

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.setPassword(it) },
                isError = (passwordError != null),
                label = { Text("Password") },
                shape = RoundedCornerShape(8.dp)
            )
        }
        AnimatedVisibility(passwordError != null) {
            passwordError?.let {
                Text(
                    text = it,
                    fontSize = errorFontSize,
                    color = Color.Red,
                )
            }
        }

        AnimatedVisibility(isRegister) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.verifyPassword.value,
                    onValueChange = { viewModel.setVerifyPassword(it) },
                    isError = (verifyPasswordError != null),
                    label = { Text("Verify Password") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
        AnimatedVisibility(verifyPasswordError != null) {
            verifyPasswordError?.let {
                Text(
                    text = it,
                    fontSize = errorFontSize,
                    color = Color.Red,
                )
            }
        }

        Button(
            onClick = {
                println("Click Login: ${viewModel.isProcessing.value}")
                if (viewModel.isProcessing.compareAndSet(expect = false, update = true)) {
                    viewModel.launchInBackground {
                        try {
                            viewModel.onClickProceed()
                        } finally {
                            viewModel.isProcessing.compareAndSet(expect = true, update = false)
                        }
                    }
                }
            },
            enabled = !viewModel.isProcessing.value,
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (isRegister) "Sign up" else "Login")
        }

        val highlightColor = MaterialTheme.colorScheme.secondary
        val signUpMessage = remember(highlightColor) {
            buildAnnotatedString {
                append("Does not have an account? Please ")
                pushStyle(SpanStyle(color = highlightColor))
                append("sign up")
                pop()
            }
        }

        val loginMessage = remember(highlightColor) {
            buildAnnotatedString {
                append("Already have an account? Please ")
                pushStyle(SpanStyle(color = highlightColor))
                append("login")
                pop()
            }
        }

        ClickableText(
            text = if (!isRegister) signUpMessage else loginMessage,
            onClick = { viewModel.onClickSwitch() },
            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
        )
    }
}