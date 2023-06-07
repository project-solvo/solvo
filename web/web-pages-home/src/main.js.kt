package org.solvo.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.Course
import org.solvo.web.document.History
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            val model = remember { HomePageViewModel() }
            val courses by model.courses.collectAsState()

            LoadableContent(isLoading = courses == null, Modifier.fillMaxSize()) {
                HomePageContent(
                    courses ?: return@LoadableContent
                )
            }
        }
    }
}

@Composable
fun HomePageContent(
    courses: List<Course>,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            text = "My Courses",
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 32.dp),
            style = MaterialTheme.typography.headlineLarge,
        )
        FlowRow(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            for (courseName in courses) {
                CourseCard(courseName)
            }
        }
    }
}

@Composable
private fun CourseCard(item: Course) {
    ElevatedCard(
        onClick = {
            History.navigate { course(item.code) }
        },
        modifier = Modifier.padding(25.dp).clickable {},
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = item.name,
            modifier = Modifier.padding(15.dp),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
