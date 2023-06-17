package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.pages.article.settings.QuestionViewModel
import org.solvo.web.settings.Section
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.snackBar.LocalTopSnackBar
import org.solvo.web.ui.snackBar.SolvoSnackbar

class QuestionSettingGroup(
    val questionCode: String,
) : ArticleSettingGroup(questionCode) {
    @Composable
    override fun NavigationIcon() {
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val model = remember(viewModel) { QuestionViewModel(viewModel) }
        val question by model.question.collectAsState(null)
        val editor = rememberRichEditorState(true, showToolbar = true)

        SideEffect {
            registerExitConfirmation {
                if (editor.contentMarkdown != question?.content) {
                    if (!window.confirm("Content not saved. Are your sure to discard it?")) {
                        return@registerExitConfirmation false
                    }
                }
                true
            }
        }

//        androidx.compose.material3.ListItem(
//            headlineText = {
//                Text("Last edited by @" )
//            },
//        )

        SimpleHeader(remember { derivedStateOf { "Question ${question?.code}" } }.value) {
            TextButton(wrapClearFocus {
                if (!requestExit()) {
                    return@wrapClearFocus
                }
                model.openQuestionPage()
            }) {
                Text("Open")
                Icon(Icons.Outlined.OpenInNew, null, Modifier.padding(start = 12.dp))
            }
        }

        val newCode by model.newCode.collectAsState()
        Section(header = {
            HeaderWithActions("Basics") {
                SaveChangesButton { model.submitBasicChanges(it) }
            }
        }) {
            val newCodeError by model.newCodeError.collectAsState()
            OutlinedTextField(
                newCode,
                { model.setNewCode(it) },
                Modifier.fillMaxWidth(),
                placeholder = { Text("Example: ia.iii") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                label = { Text("Question Code") },
                supportingText = {
                    Text(newCodeError ?: "Each question should have an unique code. ")
                },
                trailingIcon = {
                    NewCodeAvailabilityIndicator(model)
                },
                isError = newCodeError != null,
            )
        }

        Section(header = {
            HeaderWithActions("Content") {
                SaveChangesButton { model.submitContentChanges(it, editor.contentMarkdown) }
            }
        }) {
            Text("Question content is displayed on the left-hand-side of the question page.")

            Spacer(Modifier.height(24.dp))

            RichEditor(
                Modifier.height(800.dp).fillMaxWidth(),
                state = editor,
            )

            // Set content when question changed
            LaunchedEffect(question) {
                question?.content?.let { editor.setContentMarkdown(it) }
            }
        }
    }

    @Composable
    private fun NewCodeAvailabilityIndicator(model: QuestionViewModel) {
        val isNewCodeAvailable by model.isNewCodeAvailable.collectAsState()
        when (isNewCodeAvailable) {
            null -> {
                CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
            }

            true -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.primary.copy(
                            0.7f
                        )
                    ) {
                        Icon(Icons.Outlined.Check, null)
                        Text("Available", Modifier.padding(start = 6.dp, end = 12.dp))
                    }
                }
            }

            else -> {}
        }
    }

    @Composable
    private fun HeaderWithActions(
        title: String,
        actions: @Composable RowScope.() -> Unit = {},
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text(title, Modifier.align(Alignment.CenterStart))
            Row(Modifier.align(Alignment.CenterEnd)) {
                actions()
            }
        }
    }

    @Composable
    private fun SaveChangesButton(onClick: (SolvoSnackbar) -> Unit) {
        val snackbar by rememberUpdatedState(LocalTopSnackBar.current)
        val onClickUpdated by rememberUpdatedState(onClick)
        Button(wrapClearFocus { onClickUpdated(snackbar) }) {
            Text("Save Changes")
        }
    }
}