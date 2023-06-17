package org.solvo.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course
import org.solvo.model.utils.UserPermission
import org.solvo.web.document.History
import org.solvo.web.document.WebPagePaths
import org.solvo.web.settings.BasicSettingsNavigationRail
import org.solvo.web.settings.Section
import org.solvo.web.settings.SettingsPage
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
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Column(Modifier.widthIn(min = 600.dp, max = 1000.dp)) {
                        HomePageContent(
                            model
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageContent(
    model: HomePageViewModel,
) {
    val courses by model.courses.collectAsState(null)
    val currentCourse  by model.course.collectAsState(null)
    val currentArticles by model.articles.collectAsState(null)
    println("Print before")
    SettingsPage(
        pageTitle = {
            Text("My Courses")
            println("print Title")
                    },
        navigationRail = {
            NavigationRail(Modifier.fillMaxHeight()) {
                courses?.forEach{
                    val onClickUpdated by rememberUpdatedState { History.pushState { course(it.code.str) } }
                    NavigationRailItem(
                        selected = it == currentCourse,
                        icon = {},
                        onClick = { onClickUpdated()},
                        modifier = Modifier.widthIn(min = 100.dp),
                        label = { Text(it.name.str) },
                        alwaysShowLabel = true,
                    )
                    println("After rail box")
                    println(it)
                }
            }
        },
    ) {
        Section(
            header = { Text("My courses")},
        ) {
            Row {
                currentArticles?.forEach {
                    CourseCard(it.course.code.str, it)
                }
            }
        }
        Section(
            header = { Text("Add courses")},
        ) {
            Row {
                currentArticles?.forEach {
                    CourseCard(it.course.code.str, it)
                }
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


//Section(
//{ Text("Add Operator") },
//Modifier.wrapContentSize()
//) {
//
//    Row {
//        OutlinedTextField(
//            "",
//            {  },
//            Modifier.padding(vertical = 12.dp).wrapContentWidth(),
//            leadingIcon = { Icon(Icons.Outlined.Search, null) },
//            placeholder = { Text("Search users by name") },
//            trailingIcon = {
//            },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
//            keyboardActions = KeyboardActions {
//
//            },
//            shape = RoundedCornerShape(12.dp)
//        )
//    }
//
//
//}