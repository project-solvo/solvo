package org.solvo.web

import androidx.compose.runtime.remember
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.accounts.AuthenticationViewModel
import org.solvo.web.accounts.LoginSignUpContent
import org.solvo.web.ui.SolvoWindow

fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { AuthenticationViewModel() }
            LoginSignUpContent(model)
        }
    }
}
