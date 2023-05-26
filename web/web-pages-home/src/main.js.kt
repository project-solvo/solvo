package org.solvo.web

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Course
import org.solvo.web.document.SolvoWindow
import org.solvo.web.ui.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            val model = remember { HomePageViewModel() }
            val courses by model.courses
            HomePageContent(courses)
        }
    }
}

@Composable
fun HomePageContent(
    courses: List<Course>?,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            courses == null,
            enter = slideInVertically { 0 } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            Row(Modifier.fillMaxWidth().padding(start = 64.dp), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        }

        AnimatedVisibility(
            courses != null,
            enter = slideInVertically { 0 } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            // Course title
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Text(
                    text = "Courses",
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 32.dp),
                    style = MaterialTheme.typography.headlineLarge,
                )
                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (courseName in courses.orEmpty()) {
                        CourseCard(courseName)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(item: Course) {
    ElevatedCard(
        onClick = {
            window.location.href = window.location.origin + "/course.html?code=${item.code}"
        },
        modifier = Modifier.padding(25.dp).height(200.dp).width(350.dp).clickable {},
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = item.name,
            modifier = Modifier.padding(15.dp),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
