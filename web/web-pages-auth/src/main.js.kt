package org.solvo.web

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.accounts.AuthenticationContent
import org.solvo.web.accounts.AuthenticationViewModel
import org.solvo.web.document.History
import org.solvo.web.session.LocalUserViewModel
import org.solvo.web.ui.SolvoWindow

fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { AuthenticationViewModel() }
            val isLoggedIn by LocalUserViewModel.current.isLoggedIn.collectAsState(null)
            if (isLoggedIn == true) {
                LaunchedEffect(true) {
                    History.navigate { authReturnOrHome() }
                }
            }
            AuthenticationContent(model)
        }
    }
}
