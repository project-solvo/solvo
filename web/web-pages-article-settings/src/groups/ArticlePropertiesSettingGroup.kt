package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.settings.HeaderWithActions
import org.solvo.web.settings.SaveChangesButton
import org.solvo.web.settings.Section
import org.solvo.web.settings.components.AutoCheckPropertyTextField

data object ArticlePropertiesSettingGroup : ArticleSettingGroup("properties") {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Outlined.Article, null)
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val model = remember(viewModel) { ArticlePropertiesViewModel(viewModel.courseCode, viewModel.article) }

        ArticleInformationSection(model)
    }

    @Composable
    private fun ArticleInformationSection(model: ArticlePropertiesViewModel) {
        Section({
            HeaderWithActions(
                "Art" +
                        "icle Information"
            ) {
                SaveChangesButton {
                    model.backgroundScope.launch {
                        model.submitBasicChanges(it)
                    }
                }
            }
        }) {
//            AutoCheckPropertyTextField(
//                model.newCode,
//                Modifier.fillMaxWidth(),
//                placeholder = { Text("Example: 2023") },
//                label = { Text("Article Code") },
//                supportingText = { Text("Each article should have an unique code. ") },
//            )

//            Spacer(Modifier.height(12.dp))

            AutoCheckPropertyTextField(
                model.newDisplayName,
                Modifier.fillMaxWidth(),
                placeholder = { Text("Example: Peper 2023") },
                label = { Text("Article Name") },
                supportingText = { Text("Name for the article") },
            )
        }
    }
}
