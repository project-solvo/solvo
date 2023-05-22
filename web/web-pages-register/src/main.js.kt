package org.solvo.web

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.accounts.LoginSignUpContent
import org.solvo.web.accounts.RegisterLoginViewModel

fun main() {
    onWasmReady {
        Window("Solvo") {
            val model = remember { RegisterLoginViewModel() }
            LoginSignUpContent(model)
        }
    }
}
