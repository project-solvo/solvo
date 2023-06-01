package org.solvo.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.ArticleDownstream
import org.solvo.model.Course
import org.solvo.web.document.History
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            val model = remember { CoursePageViewModel() }

            val course by model.course.collectAsState(null)
            val articles by model.articles.collectAsState(null)

            LoadableContent(isLoading = course == null || articles == null, Modifier.fillMaxSize()) {
                PageContent(
                    course ?: return@LoadableContent,
                    articles ?: return@LoadableContent
                )
            }
        }
    }
}

@Composable
fun PageContent(
    course: Course,
    articles: List<ArticleDownstream>,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            text = "Courses",
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 32.dp),
            style = MaterialTheme.typography.headlineLarge,
        )
        FlowRow(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            for (article in articles) {
                CourseCard(course.code, article)
            }
        }
    }
}

@Composable
private fun CourseCard(courseCode: String, article: ArticleDownstream) {
    ElevatedCard(
        onClick = {
            History.navigateNotNull {
                article.questionIndexes.firstOrNull()?.let { code ->
                    question(courseCode, article.code, code)
                }
            }
        },
        modifier = Modifier.padding(25.dp).clickable {},
        shape = RoundedCornerShape(8.dp),
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
