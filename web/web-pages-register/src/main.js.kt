package org.solvo.web

import androidx.compose.runtime.remember
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.accounts.LoginSignUpContent
import org.solvo.web.accounts.RegisterLoginViewModel
import org.solvo.web.document.SolvoWindow

fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { RegisterLoginViewModel() }
            LoginSignUpContent(model)
        }
    }
}
