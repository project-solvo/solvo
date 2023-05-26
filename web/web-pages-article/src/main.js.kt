package org.solvo.web

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Article
import org.solvo.model.Course
import org.solvo.model.LightComment
import org.solvo.model.Question
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.CommentCard
import org.solvo.web.comments.CourseMenu
import org.solvo.web.comments.CourseMenuState
import org.solvo.web.document.History
import org.solvo.web.document.parameters.article
import org.solvo.web.document.parameters.course
import org.solvo.web.document.parameters.question
import org.solvo.web.document.parameters.rememberPathParameters
import org.solvo.web.editor.RichText
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.foundation.VerticalDraggableDivider
import org.solvo.web.ui.image.rememberImagePainter


fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { ArticlePageViewModel() }

            val menuState = remember { CourseMenuState() }
            SolvoTopAppBar {
                IconButton(onClick = { menuState.switchMenuOpen() }) {
                    Icon(Icons.Filled.Menu, null)
                }
            }

            CourseMenu(menuState, model.allArticles, onClickQuestion = { article: Article, question: Question ->
                History.navigate { question(article.course.code, article.code, question.code) }
            })


            val pathParameters = rememberPathParameters(WebPagePathPatterns.article)
            val course by pathParameters.course().collectAsState(null)
            val article by pathParameters.article().collectAsState(null)
            val question by pathParameters.question().collectAsState(null)

            LoadableContent(course == null || article == null || question == null, Modifier.fillMaxSize()) {
                ArticlePageContent(
                    course = course ?: return@LoadableContent,
                    article = article ?: return@LoadableContent,
                    question = question ?: return@LoadableContent,
                )
            }
        }
    }
}

@Composable
private fun ArticlePageContent(
    course: Course,
    article: Article,
    question: Question,
) {
    Row(Modifier.fillMaxSize()) {
        val window = LocalSolvoWindow.current
        val windowSize by window.size.collectAsState()

        var leftWidth by remember { mutableStateOf(windowSize.width * (1.0f - 0.618f)) }
        Box(Modifier.width(leftWidth)) {
            PaperView(
                courseTitle = { PaperTitle(course, article.name, question.code) },
                onChangeLayout = {
                    // TODO change article page layout
                },
                Modifier.fillMaxSize()
            ) {
                Image(
                    rememberImagePainter(
                        "https://him188.github.io/static/images/WACCLangSpec_00.png",
                        default = Icons.Outlined.Description,
                    ),
                    "Article Content",
                    Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
                )
            }
        }

        VerticalDraggableDivider(
            onDrag = { leftWidth += it },
            Modifier.fillMaxHeight(),
        )

        Column {
            ControlBar(Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxHeight().align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton({}, contentPadding = buttonContentPaddings) {
                            Icon(Icons.Default.West, "Previous")
                            Text("Previous", Modifier.padding(horizontal = 4.dp))
                        }

                        Text("2 / 4", Modifier.padding(horizontal = 16.dp), fontFamily = FontFamily.Monospace)

                        TextButton({}, contentPadding = buttonContentPaddings) {
                            Text("Next", Modifier.padding(horizontal = 4.dp))
                            Icon(Icons.Default.East, "Next")
                        }
                    }

                    Box(Modifier.align(Alignment.CenterEnd)) {
                        FilledTonalButton(
                            {},
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = buttonContentPaddings
                        ) {
                            Icon(Icons.Outlined.PostAdd, "Draft Answer", Modifier.fillMaxHeight())

                            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Draft Answer",
                                    Modifier.padding(start = 4.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                        }
                    }
                }
            }

            Box(Modifier.padding(vertical = 12.dp).padding(end = 12.dp, start = 12.dp).fillMaxSize()) {
                AnswersList()
            }
        }
    }
}

@Composable
private fun AnswersList() {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(2) {
            // TODO: 2023/5/25 view model 
            CommentCard(
                listOf(
                    LightComment(Uuid.random(), "查尔斯", "", "你是好人！"),
                    LightComment(Uuid.random(), "Commenter2", "", "[Image] Content 2"),
                ),
                Modifier.weight(1.0f, fill = true)
            ) {
                RichText(
                    """
                                Some Java code
                                ```java
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                ```
                            """.trimIndent(),
                    modifier = Modifier.weight(1.0f).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PaperTitle(
    course: Course,
    year: String,
    questionNumber: String,
) {
    Text(course.code, fontWeight = FontWeight.W800, fontSize = 22.sp)
    Text(course.name, Modifier.padding(start = 4.dp), fontWeight = FontWeight.W800, fontSize = 22.sp)
    Text(year, Modifier.padding(start = 16.dp), fontWeight = FontWeight.W700, fontSize = 18.sp)
    Text(questionNumber, Modifier.padding(start = 6.dp), fontWeight = FontWeight.W600, fontSize = 16.sp)
}

@Composable
private fun PaperView(
    courseTitle: @Composable RowScope. () -> Unit,
    onChangeLayout: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        ControlBar(Modifier.fillMaxWidth()) {
            courseTitle()
            Spacer(Modifier.weight(1f))
            IconButton(onChangeLayout) {
                if (isExpanded) {
                    Icon(Icons.Default.Compress, "Compress")
                } else {
                    Icon(Icons.Default.Expand, "Expand")
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            content()
        }
    }
}

object ControlBarScope {
    val buttonContentPaddings = PaddingValues(
        start = 12.dp,
        top = 3.dp,
        end = 12.dp,
        bottom = 3.dp
    )
}

@Composable
private fun ControlBar(
    modifier: Modifier = Modifier,
    content: @Composable context(ControlBarScope) RowScope.() -> Unit,
) {
//    val shape = RoundedCornerShape(12.dp)
    Surface(
        Modifier,
//            .padding(12.dp)
//            .clip(shape)
//            .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
//            .border(color = MaterialTheme.colorScheme.outline, width = 1.dp, shape = shape)
        tonalElevation = 1.dp
    ) {
        Row(
            modifier.padding(horizontal = 12.dp, vertical = 8.dp).height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            content(ControlBarScope, this)
        }
    }
}