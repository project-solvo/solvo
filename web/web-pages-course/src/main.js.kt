package org.solvo.web

//@OptIn(DelicateCoroutinesApi::class)
//fun main() {
//    // Currently, jump to first article
//
//    GlobalScope.launch {
//        val code = PathParameters(WebPagePaths.courses())[WebPagePathPatterns.VAR_COURSE_CODE] ?: return@launch
//        val article = client.courses.getAllArticles(code)?.firstOrNull() ?: return@launch
//        History.navigate { article(code, article.name) } // TODO: 2023/5/29 navigate to article 
//    }
//}

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
import org.solvo.model.Course
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.web.document.History
import org.solvo.web.document.parameters.course
import org.solvo.web.document.parameters.rememberPathParameters
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            val path = rememberPathParameters(WebPagePathPatterns.course)
            val course by path.course().collectAsState(null)

            LoadableContent(isLoading = course == null, Modifier.fillMaxSize()) {
                PageContent(
                    course ?: return@LoadableContent
                )
            }
        }
    }
}

@Composable
fun PageContent(
    course: Course,
//    courses: List<String>,
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
            for (articleCode in listOf("2022-2023", "2021-2022", "2020-2021")) {
                CourseCard(course.code, articleCode, course.name)
            }
        }
    }
}

@Composable
private fun CourseCard(courseCode: String, articleCode: String, name: String) {
    ElevatedCard(
        onClick = {
            History.navigate {
                question(courseCode, articleCode, "1a")
            }
        },
        modifier = Modifier.padding(25.dp).clickable {},
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = articleCode,
            modifier = Modifier.padding(15.dp),
            style = MaterialTheme.typography.headlineSmall,
        )

        val questions = remember {
            listOf("1a", "1b", "1c", "1d", "1e", "2a", "2b", "2c", "2d")
        }

        FlowRow {
            for (question in questions) {
                SuggestionChip({}, { Text(question) }, Modifier.padding(all = 8.dp))
            }
        }
    }
}
