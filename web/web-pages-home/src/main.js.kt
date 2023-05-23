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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Comment
import org.solvo.model.Course
import org.solvo.model.LightComment
import org.solvo.model.User
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.CommentCard
import org.solvo.web.document.SolvoWindow
import org.solvo.web.ui.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            CommentCard(
                Comment(
                    Uuid.random(), Uuid.random(),
                    User(Uuid.random(), "Author", ""),
                    listOf(
                        LightComment(Uuid.random(), "Commenter1", "", "Content 1"),
                        LightComment(Uuid.random(), "Commenter2", "", "[Image] Content 2"),
                    ),
                )
            )
//            HomePageContent()
        }
    }
}

@Composable
fun HomePageContent() {
    SolvoTopAppBar()
    Column(
        modifier = Modifier.fillMaxSize().padding(100.dp).verticalScroll(rememberScrollState())
    ) {
        // Course title
        Text(
            text = "Courses",
            modifier = Modifier.padding(50.dp),
            style = MaterialTheme.typography.headlineLarge,
        )
        val courses = mutableListOf<Course>()
        for (i in 50001..<50012) {
            courses.add(Course(i.toString(), i.toString()))
        }

        FlowRow(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
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
