package org.solvo.web

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.User
import org.solvo.model.utils.UserPermission
import org.solvo.web.document.History
import org.solvo.web.requests.client
import org.solvo.web.session.currentUser
import org.solvo.web.session.currentUserHasPermission
import org.solvo.web.session.isLoggedInOrNull
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.foundation.rememberBackgroundScope
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import org.w3c.dom.events.Event
import org.w3c.files.File
import kotlin.time.Duration.Companion.seconds

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            if (isLoggedInOrNull() == false) {
                SideEffect {
                    History.navigate { auth() }
                }
            }

            val user = currentUser
            LoadableContent(isLoading = user == null, Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Column(Modifier.widthIn(min = 600.dp, max = 800.dp)) {
                        UserPageContent(user ?: return@LoadableContent)
                    }
                }
            }
        }
    }
}

@Composable
fun UserPageContent(
    user: User,
) {
    Text("Welcome, " + user.username, style = MaterialTheme.typography.headlineLarge)

    Spacer(Modifier.height(40.dp))

    Column {
        Text(
            text = "Personal Details",
            modifier = Modifier.padding(vertical = 12.dp),
            style = MaterialTheme.typography.headlineLarge,
        )

        var showUploadAvatarPopup by remember { mutableStateOf(false) }
        Button(wrapClearFocus { showUploadAvatarPopup = true }) {
            Text("Update Avatar")
        }

        val backgroundScope = rememberBackgroundScope()
        val snackbar = LocalTopSnackBar.current

        PopupWindow(
            showUploadAvatarPopup,
            Modifier.width(500.dp).wrapContentHeight(),
            alignment = Alignment.Center,
            onDismissRequest = { showUploadAvatarPopup = false },
            bottomButtons = {
                Button({ showUploadAvatarPopup = false }, Modifier.align(Alignment.CenterEnd)) {
                    Text("Cancel")
                }
            }
        ) {
            var isUploading by remember { mutableStateOf(false) }

            Text("Drag Image Here!")

            if (isUploading) {
                CircularProgressIndicator()
            }
            DraggableArea { file ->
                if (isUploading) return@DraggableArea

                backgroundScope.launch {
                    isUploading = true
                    runCatching {
                        client.accounts.uploadAvatar(file)
                    }.fold(
                        onSuccess = {
                            launch {
                                snackbar.showSnackbar("Successfully uploaded avatar")
                            }
                            launch {
                                delay(0.5.seconds)
                                History.refresh()
                            }
                        },
                        onFailure = { snackbar.showSnackbar("Upload failed") }
                    )
                    showUploadAvatarPopup = false
                }
            }
        }

        if (currentUserHasPermission(UserPermission.ROOT)) {
            Text(
                text = "Administration Settings",
                modifier = Modifier.padding(vertical = 12.dp),
                style = MaterialTheme.typography.headlineLarge,
            )

            Button(wrapClearFocus { History.navigate { settingsAdmin(null) } }) {
                Text("Open")
                Icon(Icons.Outlined.OpenInNew, null, Modifier.padding(start = 12.dp))
            }
        }
    }
}

@Composable
private fun DraggableArea(
    onDropFile: (File) -> Unit,
) {
    val window = LocalSolvoWindow.current
    DisposableEffect(true) {
        val stopDragListener: (Event) -> Unit = { event ->
            event.preventDefault()
            event.stopPropagation()
        }
        window.canvas.addEventListener("dragover", stopDragListener)
        window.canvas.addEventListener("dragEnter".lowercase(), { event ->
            event.preventDefault()
            event.stopPropagation()
        })
        val handleDropListener: (Event) -> Unit = { event ->
            event.preventDefault()
            event.stopPropagation()
            val files = event.asDynamic().files ?: event.asDynamic().dataTransfer.files
            val file: File = files[0] as File
            onDropFile(file)
        }
        window.canvas.addEventListener("drop", handleDropListener)
        onDispose {
            window.canvas.removeEventListener("dragover", stopDragListener)
            window.canvas.removeEventListener("dragEnter".lowercase(), stopDragListener)
            window.canvas.removeEventListener("dragEnter".lowercase(), handleDropListener)
        }
    }
}

@Composable
private fun PopupWindow(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart,
    offset: IntOffset = IntOffset(0, 0),
    focusable: Boolean = false,
    onDismissRequest: (() -> Unit)? = null,
    onPreviewKeyEvent: ((KeyEvent) -> Boolean) = { false },
    onKeyEvent: ((KeyEvent) -> Boolean) = { false },
    bottomButtons: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (isVisible) {
        Popup(alignment, offset, focusable, onDismissRequest, onPreviewKeyEvent, onKeyEvent) {
            Column(
                modifier
                    .animateContentSize()
                    .shadow(1.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(horizontal = 18.dp).padding(top = 18.dp, bottom = 12.dp)) {
                    content()

                    if (bottomButtons != null) {
                        Box(
                            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
                        ) {
                            bottomButtons()
                        }
                    }
                }
            }
        }
    }
}