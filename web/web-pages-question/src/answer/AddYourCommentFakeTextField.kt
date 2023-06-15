package org.solvo.web.answer

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.solvo.model.utils.NonBlankString
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.OutlinedTextField
import org.solvo.web.ui.foundation.ifThenElse
import org.solvo.web.ui.modifiers.clickable

@Composable
fun AddYourCommentTextField(
    onSend: (NonBlankString) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEditorLoaded by remember { mutableStateOf(false) }
    var isEditorVisible by remember { mutableStateOf(false) }
    var edited by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (!isEditorVisible) {
        val onClick = {
            client.checkLoggedIn()
            isEditorLoaded = true
            isEditorVisible = true
        }
        OutlinedTextField(
            "", { onClick() },
            modifier
                .height(48.dp)
                .clickable(indication = null, onClick = onClick)
                .padding(top = 6.dp, bottom = 6.dp) // inner
                .fillMaxWidth(),
            readOnly = true,
            placeholder = {
                Text(
                    if (edited) "Continue editing your comment..." else "Add your comment...",
                    Modifier.clickable(indication = null, onClick = onClick)
                        .fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
            )
        )
    }

    if (isEditorLoaded) {
        val onSendUpdated by rememberUpdatedState(onSend)
        Column(
            Modifier.fillMaxWidth().animateContentSize()
                .ifThenElse(isEditorVisible, then = { wrapContentHeight() }, `else` = { height(0.dp) })
        ) {
            val state = rememberRichEditorState(true, showToolbar = true)
            RichEditor(modifier.fillMaxWidth().height(250.dp), state = state)
            Row(Modifier.padding(top = 8.dp).align(Alignment.End)) {
                TextButton(
                    {
                        isEditorVisible = false
                        edited = state.contentMarkdown?.isNotBlank() == true
                    },
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.secondary)
                }
                Button({
                    state.contentMarkdown
                        ?.let { NonBlankString.fromStringOrNull(it) }
                        ?.let(onSendUpdated)
                    scope.launch { state.setContentMarkdown("") }
                    isEditorVisible = false
                }) {
                    Text("Send")
                }
            }
        }
    }
}
