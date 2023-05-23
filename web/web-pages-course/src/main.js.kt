package org.solvo.web

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.document.SolvoWindow
import org.solvo.web.ui.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            HomePageContent()
        }
    }
}

@Composable
fun HomePageContent() {
    SolvoTopAppBar()
    Column(
        modifier = Modifier.fillMaxSize().padding(100.dp).verticalScroll(rememberScrollState())
    ) {

    }
}