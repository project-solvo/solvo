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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.utils.NonBlankString
import org.solvo.web.editor.RichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import org.solvo.web.viewModel.LoadingUuidItem

@Composable
fun AnswerListControlBar(
    pagingState: ExpandablePagingState<LoadingUuidItem<CommentDownstream>>,
    model: DraftAnswerControlBarState,
    draftAnswerEditor: RichEditorState,
    question: QuestionDownstream,
    backgroundScope: CoroutineScope,
) {
    val pagingStateUpdated by rememberUpdatedState(pagingState)
    val uiScope = rememberCoroutineScope()

    ControlBar(Modifier.fillMaxWidth()) {
        if (pagingStateUpdated.isExpanded.value) {
            ControlBarButton(
                icon = { Icon(Icons.Outlined.ArrowBack, null) },
                text = { Text("Go Back") },
                onClick = { uiScope.launch(start = CoroutineStart.UNDISPATCHED) { pagingStateUpdated.switchExpanded() } },
                shape = buttonShape,
                contentPadding = buttonContentPaddings,
                colors = ButtonDefaults.filledTonalButtonColors(
                    MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        } else {
            val isDraftButtonsVisible by model.isDraftButtonsVisible.collectAsState(false)
            val draftKind by model.draftKind.collectAsState(null)
            AnimatedVisibility(isDraftButtonsVisible) {
                val entry = DraftKind.ANSWER
                ControlBarButton(
                    icon = {
                        Icon(entry.icon, null, Modifier.fillMaxHeight())
                    },
                    text = {
                        Text(
                            remember(entry) { "Draft ${entry.displayName}" },
                            fontSize = CONTROL_BUTTON_FONT_SIZE
                        )
                    },
                    onClick = {
                        client.checkLoggedIn()
                        model.startDraft(entry)
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                    contentPadding = buttonContentPaddings,
                    shape = buttonShape,
                )
            }

            AnimatedVisibility(isDraftButtonsVisible) {
                PostThoughtsButton(model)
            }

            AnimatedVisibility(draftKind != null) {
                val snackBar by rememberUpdatedState(LocalTopSnackBar.current)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlBarButton(
                        icon = { Icon(Icons.Outlined.FolderOff, null, Modifier.fillMaxHeight()) },
                        text = { Text("Cancel", fontSize = CONTROL_BUTTON_FONT_SIZE, fontWeight = FontWeight.W500) },
                        onClick = {
                            model.stopDraft()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                        contentPadding = buttonContentPaddings,
                        shape = buttonShape,
                    )
                    ControlBarButton(
                        icon = { Icon(draftKind?.icon ?: return@ControlBarButton, null, Modifier.fillMaxHeight()) },
                        text = {
                            Text(
                                remember(draftKind) { "Post ${draftKind?.displayName}" },
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
                                    currentDraftKind?.let {
                                        client.comments.post(
                                            question.coid,
                                            CommentUpstream(
                                                NonBlankString.fromStringOrNull(
                                                    draftAnswerEditor.contentMarkdown ?: ""
                                                )
                                                    ?: return@launch
                                            ),
                                            it.toCommentKind(),
                                        )
                                    }
                                }
                                model.stopDraft()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        contentPadding = buttonContentPaddings,
                        shape = buttonShape,
                    )

                    if (draftKind != null) {
                        DraftKindDescription(draftKind, model)
                    }
                }
            }
        }
    }
}

@Composable
private fun DraftKindDescription(draftKind: DraftKind?, model: DraftAnswerControlBarState) {
    Icon(Icons.Outlined.AutoAwesome, null, Modifier.padding(vertical = 2.dp))
    if (draftKind == DraftKind.THOUGHT) {
        Text("You can add partial answers, thinking, and any ideas. ")
        Text(
            "Convert to answer", Modifier.cursorHoverIcon(CursorIcon.POINTER).clickable {
                model.startDraft(DraftKind.ANSWER)
            },
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    } else { // answer
        Row {
            Text("You are adding an answer. ")
            Text(
                "Convert to thought", Modifier.cursorHoverIcon(CursorIcon.POINTER).clickable {
                    model.startDraft(DraftKind.THOUGHT)
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
            { Icon(DraftKind.THOUGHT.icon, null, Modifier.fillMaxHeight()) },
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
                model.startDraft(DraftKind.THOUGHT)
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
