package org.solvo.web.editor

import androidx.compose.runtime.*

@Composable
fun rememberRichEditorLoadedState() = remember { RichEditorLoadedState() }

class RichEditorLoadedState {
    var isEditorLoaded: Boolean by mutableStateOf(false)
    var isTextChanged: Boolean by mutableStateOf(false)

    val onEditorLoaded: () -> Unit = {
        isEditorLoaded = true
    }
    val onTextChanged: () -> Unit = {
        isTextChanged = true
    }

    val isReady by derivedStateOf {
        isEditorLoaded && isTextChanged
    }
}