package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.solvo.web.document.History
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.pages.article.settings.QuestionViewModel
import org.solvo.web.settings.Section
import org.solvo.web.ui.foundation.wrapClearFocus

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
                History.navigate {
                    question(
                        model.courseCode.value,
                        model.articleCode.value,
                        model.questionCode.value ?: return@wrapClearFocus
                    )
                }
            }) {
                Text("Open")
                Icon(Icons.Outlined.OpenInNew, null, Modifier.padding(start = 12.dp))
            }
        }

        Section(header = { Text("Content") }) {
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
}