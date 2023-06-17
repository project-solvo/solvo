package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.pages.article.settings.QuestionViewModel

class QuestionSettingGroup(
    val questionCode: String,
) : ArticleSettingGroup(questionCode, "") {
    @Composable
    override fun NavigationIcon() {
    }

    @Composable
    override fun ColumnScope.Header(viewModel: PageViewModel) {
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val model = remember(viewModel) { QuestionViewModel(viewModel) }
        val question by model.question.collectAsState(null)
        SimpleHeader(questionCode)
        Text("Question ${question?.code}")
    }
}