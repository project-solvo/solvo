package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.ArticleEditRequest
import org.solvo.model.api.communication.isEmpty
import org.solvo.model.utils.NonBlankString
import org.solvo.model.utils.nonBlankOrNull
import org.solvo.web.document.History
import org.solvo.web.pages.article.settings.groups.ArticlePropertiesViewModel
import org.solvo.web.requests.client
import org.solvo.web.settings.Section
import org.solvo.web.settings.SettingGroup
import org.solvo.web.settings.components.AutoCheckPropertyTextField
import org.solvo.web.viewModel.launchInBackground

class CourseSettingGroup(pathName: String, val name: NonBlankString) : SettingGroup<PageViewModel>(pathName) {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Default.Class, "Course")
    }
    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val courseViewModel = remember { CourseViewModel(viewModel.model) }
        val articles by courseViewModel.model.articles.collectAsState(null)
        Section(
            header = { Text("Article") },
        ) {
            articles?.forEach {
                CourseCard(it.course.code.str, it)
            }
        }
        Section(
            header = { Text("Add new article") },
        ) {
            Column(
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                AddPaperContent(courseViewModel)
            }
        }
    }

    @Composable
    private fun AddPaperContent(
        courseViewModel: CourseViewModel,
    ) {
        val articlePropertyViewModel = remember(courseViewModel) {
            ArticlePropertiesViewModel(courseCode = courseViewModel.model.courseCode, originalArticle = MutableStateFlow(null))
        }
        AutoCheckPropertyTextField(
            articlePropertyViewModel.newCode,
            Modifier.fillMaxWidth(),
            placeholder = { Text("Example: 2023") },
            label = { Text("Article Code") },
            supportingText = { Text("Each article should have an unique code. ") },
        )

        Spacer(Modifier.height(12.dp))

        AutoCheckPropertyTextField(
            articlePropertyViewModel.newDisplayName,
            Modifier.fillMaxWidth(),
            placeholder = { Text("Example: Paper 2023") },
            label = { Text("Article Name") },
            supportingText = { Text("Name for the article") },
        )

        Row {
            FilledTonalButton(
                onClick = {
                    courseViewModel.launchInBackground {
                        val targetArticleCode = articlePropertyViewModel.newCode.value
                        client.articles.addArticle(courseViewModel.model.courseCode.value, targetArticleCode)

                        val request = ArticleEditRequest(
                            code = targetArticleCode.nonBlankOrNull,
                            displayName = articlePropertyViewModel.newDisplayName.value.nonBlankOrNull,
                        )

                        if (!request.isEmpty()) {
                            client.articles.update(
                                courseViewModel.model.courseCode.value, targetArticleCode, request
                            )
                        }
                    }
                },
                modifier = Modifier.width(160.dp).padding(vertical = 4.dp).height(40.dp).align(Alignment.CenterVertically),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            ) {
                Text("Add new article")
            }
        }

    }

    @Composable
    private fun CourseCard(courseCode: String, article: ArticleDownstream) {
        Row(
            modifier = Modifier.padding(25.dp),
        ) {
            Text(
                text = article.displayName,
                modifier = Modifier.padding(15.dp),
                style = MaterialTheme.typography.headlineSmall,
            )

            val questions = remember(article) { article.questionIndexes }

            FlowRow {
                for (question in questions) {
                    SuggestionChip({
                        History.navigate { question(courseCode, article.code, question) }
                    }, { Text(question) }, Modifier.padding(all = 8.dp))
                }
            }
        }
    }

}