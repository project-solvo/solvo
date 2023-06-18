package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.settings.HeaderWithActions
import org.solvo.web.settings.SaveChangesButton
import org.solvo.web.settings.Section
import org.solvo.web.settings.SimpleHeader
import org.solvo.web.settings.components.AutoCheckPropertyTextField
import org.solvo.web.ui.foundation.wrapClearFocus

class QuestionSettingGroup(
    questionCode: String,
) : ArticleSettingGroup(questionCode) {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Outlined.Quiz, null)
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val model = remember(viewModel) { QuestionSettingsViewModel(viewModel, viewModel.settingGroupName) }
        val question by model.originalQuestion.collectAsState(null)
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

        Section(header = {
            HeaderWithActions("Question Information") {
                SaveChangesButton { model.submitBasicChanges(it) }
            }
        }) {
            QuestionCodeTextField(model)
        }

        Section(header = {
            HeaderWithActions("Content") {
                if (model.isFullscreen.collectAsState().value) {
                    TextButton({ model.setFullscreen(false) }) {
                        Icon(Icons.Outlined.FullscreenExit, null)
                        Text("Close Fullscreen")
                    }
                } else {
                    TextButton({ model.setFullscreen(true) }) {
                        Icon(Icons.Outlined.Fullscreen, null)
                        Text("Fullscreen")
                    }
                }
                Spacer(Modifier.width(12.dp))
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

    companion object {
        @Composable
        fun QuestionCodeTextField(model: QuestionSettingsViewModel) {
            AutoCheckPropertyTextField(
                model.newCode,
                Modifier.fillMaxWidth(),
                placeholder = { Text("Example: ia.iii") },
                label = { Text("Question Code") },
                supportingText = {
                    Text("Each question should have an unique code. ")
                },
            )
        }
    }
}