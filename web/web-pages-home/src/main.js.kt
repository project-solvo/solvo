package org.solvo.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.web.document.History
import org.solvo.web.pages.article.settings.groups.ArticlePropertiesViewModel
import org.solvo.web.settings.Section
import org.solvo.web.settings.SettingsPage
import org.solvo.web.settings.components.AutoCheckPropertyTextField
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
    SettingsPage(
        pageTitle = { Text("My Courses") },
        navigationRail = {
            Column(
                Modifier
                    .padding(end = 48.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .fillMaxHeight()
            ) {
                Text(
                    "Courses",
                    Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                )
                courses?.forEach{
                    ListItem(
                        leadingContent = {},
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                History.pushState { course(it.code.str) }
                            }.width(200.dp).padding(vertical = 4.dp),
                        tonalElevation = if (it == currentCourse) 2.dp else 0.dp,
                        headlineText = { Text(it.name.str) },
                    )
//                    NavigationRailItem(
//                        selected = it == currentCourse,
//                        icon = {},
//                        onClick = { onClickUpdated()},
//                        modifier = Modifier.widthIn(min = 100.dp),
//                        label = { Text(it.name.str) },
//                        alwaysShowLabel = true,
//                    )
                }
                FilledTonalButton(
                    onClick = {},
                    modifier = Modifier.width(200.dp).padding(vertical = 4.dp).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                ) {
                    Text("Add new course")
                }
            }
        },
        Modifier.verticalScroll(rememberScrollState())
    ) {
        Section(
            header = { Text("Article")},
        ) {
            FlowRow {
                currentArticles?.forEach {
                    CourseCard(it.course.code.str, it)
                }
            }
        }
        Section(
            header = { Text("Add new article")},
        ) {
            Column(
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                AddPaperContent(model)
            }
        }
    }

}

@Composable
private fun AddPaperContent(
    model: HomePageViewModel,
) {
    val articlePropertyViewModel = remember(model) {
        ArticlePropertiesViewModel(courseCode = model.courseCode, originalArticle = MutableStateFlow(null))
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

    Row{
        FilledTonalButton(
            onClick = {},
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
