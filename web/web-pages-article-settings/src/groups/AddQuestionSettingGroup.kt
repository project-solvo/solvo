package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.settings.HeaderWithActions
import org.solvo.web.settings.SaveChangesButton
import org.solvo.web.settings.Section

data object AddQuestionSettingGroup : ArticleSettingGroup("add") {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Outlined.Add, null)
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val model = remember(viewModel) { QuestionSettingsViewModel(viewModel, MutableStateFlow(null)) }

        Section({
            HeaderWithActions("Add Question") {
                SaveChangesButton(
                    text = {
                        Text("Submit")
                    }
                ) {
                    model.submitBasicChanges(it)
                    model.navigateSettingGroup(model.newCode.value)
                }
            }
        }) {
            QuestionSettingGroup.QuestionCodeTextField(model)
        }
    }
}
