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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginSignUpContent(viewModel: RegisterLoginViewModel) {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
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
                var username by viewModel.username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.height(60.dp),
                    label = { Text("Username") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var password by viewModel.password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.height(60.dp),
                    label = { Text("Password") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            val isRegister by viewModel.isRegister.collectAsState()
            AnimatedVisibility(isRegister) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var verifyPassword by viewModel.verifyPassword
                    OutlinedTextField(
                        value = verifyPassword,
                        onValueChange = { verifyPassword = it },
                        modifier = Modifier.height(60.dp),
                        label = { Text("Verify Password") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Button(
                onClick = { viewModel.onClickProceed() },
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