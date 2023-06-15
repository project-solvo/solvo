package org.solvo.web

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.solvo.model.api.communication.CommentEditRequest
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.communication.isEmpty
import org.solvo.model.utils.NonBlankString
import org.solvo.web.comments.commentCard.components.CONTROL_BUTTON_FONT_SIZE
import org.solvo.web.editor.RichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.foundation.wrapClearFocus1
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon
import org.solvo.web.ui.snackBar.LocalTopSnackBar

@Composable
fun AnswerListControlBar(
    model: QuestionPageViewModel,
    draftAnswerEditor: RichEditorState,
    question: QuestionDownstream,
    backgroundScope: CoroutineScope,
) {
    val controlBar = model.controlBarState

    ControlBar(Modifier.fillMaxWidth()) {
        val isExpanded by model.isExpanded.collectAsState()
        if (isExpanded) {
            ControlBarButton(
                icon = { Icon(Icons.Outlined.ArrowBack, null) },
                text = { Text("Go Back") },
                onClick = { model.collapse() },
                shape = buttonShape,
                contentPadding = buttonContentPaddings,
                colors = ButtonDefaults.filledTonalButtonColors(
                    MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        } else {
            val isDraftButtonsVisible by controlBar.isDraftButtonsVisible.collectAsState(false)
            val draftKind by controlBar.draftKind.collectAsState(null)
            AnimatedVisibility(isDraftButtonsVisible) {
                val entry = DraftKind.Answer
                ControlBarButton(
                    icon = {
                        Icon(entry.icon, null, Modifier.fillMaxHeight())
                    },
                    text = {
                        Text(
                            remember(entry) { "Draft ${entry.displayName}" },
                        )
                    },
                    onClick = {
                        client.checkLoggedIn()
                        controlBar.startDraft(entry)
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                    contentPadding = buttonContentPaddings,
                    shape = buttonShape,
                )
            }

            AnimatedVisibility(isDraftButtonsVisible) {
                PostThoughtsButton(controlBar)
            }

            AnimatedVisibility(draftKind != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlBarButton(
                        icon = { Icon(Icons.Outlined.FolderOff, null, Modifier.fillMaxHeight()) },
                        text = { Text("Cancel", fontSize = CONTROL_BUTTON_FONT_SIZE, fontWeight = FontWeight.W500) },
                        onClick = { controlBar.stopDraft() },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                        contentPadding = buttonContentPaddings,
                        shape = buttonShape,
                    )

                    PostButton(draftAnswerEditor, backgroundScope, question, controlBar)

                    (draftKind as? DraftKind.New)?.let { DraftKindDescription(it, controlBar) }

                    (draftKind as? DraftKind.Edit)?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Anonymous: ")
                            Switch(
                                controlBar.isAnonymousNew.value,
                                wrapClearFocus1 { controlBar.setAnonymous(it) },
                                Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlBarScope.PostButton(
    draftAnswerEditor: RichEditorState,
    backgroundScope: CoroutineScope,
    question: QuestionDownstream,
    model: DraftAnswerControlBarState
) {
    val draftKind by model.draftKind.collectAsState(null)
    val snackBar by rememberUpdatedState(LocalTopSnackBar.current)
    ControlBarButton(
        icon = { Icon(draftKind?.icon ?: return@ControlBarButton, null, Modifier.fillMaxHeight()) },
        text = {
            Text(
                draftKind?.displayNameInPostButton ?: "",
                fontSize = CONTROL_BUTTON_FONT_SIZE
            )
        },
        onClick = {
            val currentDraftKind = draftKind // save before `model.post` clears it
            client.checkLoggedIn()
            if (draftAnswerEditor.contentMarkdown == "") {
                backgroundScope.launch {
                    snackBar.showSnackbar(
                        "${currentDraftKind?.displayName} can not be empty", withDismissAction = true
                    )
                }
            } else {
                backgroundScope.launch {
                    currentDraftKind?.let { kind ->
                        handlePost(kind, question, draftAnswerEditor, model.isAnonymousNew.value)
                    }
                }
                model.stopDraft()
            }
        },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        contentPadding = buttonContentPaddings,
        shape = buttonShape,
    )
}

private suspend fun handlePost(
    kind: DraftKind,
    question: QuestionDownstream,
    draftAnswerEditor: RichEditorState,
    isAnonymousNew: Boolean,
) {
    if (kind is DraftKind.Edit) {
        val original = kind.comment
        val request = CommentEditRequest(
            content = createNewContent(draftAnswerEditor.contentMarkdown, original.content),
            anonymity = isAnonymousNew.takeIf { it != original.anonymity },
        )
        if (request.isEmpty()) {
            return // nothing to change
        }
        client.comments.edit(original.coid, request)
        draftAnswerEditor.clearContent()
    } else {
        val request = CommentUpstream(
            NonBlankString.fromStringOrNull(
                draftAnswerEditor.contentMarkdown ?: ""
            ) ?: return // nothing to post
        )
        client.comments.post(question.coid, request, kind.toCommentKind()!!)
        draftAnswerEditor.clearContent()
    }
}

private fun createNewContent(
    newContent: String?,
    originalContent: String,
): NonBlankString? {
    return NonBlankString.fromStringOrNull(newContent ?: "").takeIf { it?.str != originalContent }
}

@Composable
private fun DraftKindDescription(draftKind: DraftKind.New, model: DraftAnswerControlBarState) {
    Icon(Icons.Outlined.AutoAwesome, null, Modifier.padding(vertical = 2.dp))
    if (draftKind == DraftKind.Thought) {
        Text("You can add partial answers, thinking, and any ideas. ")
        Text(
            "Convert to answer", Modifier.cursorHoverIcon(CursorIcon.POINTER).clickable {
                model.startDraft(DraftKind.Answer)
            },
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    } else { // answer
        Row {
            Text("You are adding an answer. ")
            Text(
                "Convert to thought", Modifier.cursorHoverIcon(CursorIcon.POINTER).clickable {
                    model.startDraft(DraftKind.Thought)
                },
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
fun ControlBarButton(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
) {
    val onClickUpdated by rememberUpdatedState(onClick)
    FilledTonalButton(
        onClick = wrapClearFocus(onClickUpdated),
        modifier,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            icon()
        }

        Box(Modifier.padding(start = 4.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            ProvideTextStyle(TextStyle(fontSize = CONTROL_BUTTON_FONT_SIZE)) {
                text()
            }
        }
    }
}

@Composable
private fun ControlBarScope.PostThoughtsButton(model: DraftAnswerControlBarState) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "or",
            fontWeight = FontWeight.W600,
            fontSize = CONTROL_BUTTON_FONT_SIZE
        )

        ControlBarButton(
            { Icon(DraftKind.Thought.icon, null, Modifier.fillMaxHeight()) },
            {
                Text(
                    "Just share your ideas",
//                        Modifier.padding(start = 6.dp),
//                        textDecoration = TextDecoration.Underline,
//                        fontWeight = W500,
//                        fontSize = CONTROL_BUTTON_FONT_SIZE + 1.sp
                )
            },
            {
                client.checkLoggedIn()
                model.startDraft(DraftKind.Thought)
            },
            shape = buttonShape,
            contentPadding = buttonContentPaddings,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    }
}
