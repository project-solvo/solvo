package org.solvo.web

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.accounts.LoginContent

fun main() {
    onWasmReady {
        Window("Solvo") {
            MainContent()
        }
    }
}

@Composable
private fun MainContent() {
    LoginContent()
}
