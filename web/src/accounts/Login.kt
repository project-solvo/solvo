package org.solvo.web.accounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.solvo.web.requests.backgroundScope

@Composable
fun LoginSignUpContent(viewModel: RegisterLoginViewModel) {
    MaterialTheme {
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
                fontStyle = FontStyle.Normal,
            )

            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.username.value,
                    onValueChange = { viewModel.setUsername(it) },
                    label = { Text("Username") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
            AnimatedVisibility(usernameError != null) {
                usernameError?.let { Text(it) }
            }

            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("Password") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
            AnimatedVisibility(passwordError != null) {
                passwordError?.let { Text(it) }
            }

            AnimatedVisibility(isRegister) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.verifyPassword.value,
                        onValueChange = { viewModel.setVerifyPassword(it) },
                        label = { Text("Verify Password") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            AnimatedVisibility(verifyPasswordError != null) {
                verifyPasswordError?.let { Text(it) }
            }

            Button(
                onClick = {
                    println("Click Login: ${viewModel.isProcessing.value}")
                    if (viewModel.isProcessing.compareAndSet(expect = false, update = true)) {
                        backgroundScope.launch {
                            viewModel.onClickProceed()
                            viewModel.isProcessing.compareAndSet(expect = true, update = false)
                        }
                    }
                },
                enabled = !viewModel.isProcessing.value,
                modifier = Modifier.padding(10.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isRegister) "Sign up" else "Login")
            }

            val signUpMessage = buildAnnotatedString {
                append("Does not have an account? Please ")
                pushStyle(SpanStyle(color = Color.Blue))
                append("sign up")
                pop()
            }

            val loginMessage = buildAnnotatedString {
                append("Already have an account? Please ")
                pushStyle(SpanStyle(color = Color.Blue))
                append("login")
                pop()
            }

            ClickableText(
                text = if (!isRegister) signUpMessage else loginMessage,
                onClick = { viewModel.onClickSwitch() },
            )

        }
    }
}