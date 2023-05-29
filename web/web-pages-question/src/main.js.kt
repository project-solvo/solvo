package org.solvo.web

import ControlBar
import ControlBarScope.buttonContentPaddings
import PagingContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.*
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

            CourseMenu(
                menuState,
                model.allArticles,
                onClickQuestion = { article: ArticleDownstream, question: QuestionDownstream ->
                    History.navigate {
                        question(
                            article.course.code,
                            article.name,
                            question.code
                        )
                    } // TODO: 2023/5/29 navigate article
                })


            val pathParameters = rememberPathParameters(WebPagePathPatterns.question)
            val course by pathParameters.course().collectAsState(null)
            val article by pathParameters.article().collectAsState(null)
            val question by pathParameters.question().collectAsState(null)

            LoadableContent(course == null || article == null || question == null, Modifier.fillMaxSize()) {
                ArticlePageContent(
                    course = course ?: return@LoadableContent,
                    article = article ?: return@LoadableContent,
                    question = question ?: return@LoadableContent,
                    model
                )
            }
        }
    }
}

@Composable
private fun ArticlePageContent(
    course: Course,
    article: ArticleDownstream,
    question: QuestionDownstream,
    state: ArticlePageViewModel,
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
            PagingContent(state, question.answers) {
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    FilledTonalButton(
                        onClick = {},
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
            Box(Modifier.padding(top = 12.dp).padding(end = 12.dp, start = 12.dp).fillMaxSize()) {
                AnswersList()
            }
        }
    }
}

@Composable
private fun AnswersList() {
    val scrollState = rememberScrollState()
    Column(
        Modifier.verticalScroll(scrollState).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(10) {
            // TODO: 2023/5/25 view model 
            CommentCard(
                remember {
                    listOf(
                        LightCommentDownstream(User(id = Uuid.random(), "查尔斯", null), "你是好人！"),
                        LightCommentDownstream(User(id = Uuid.random(), "Commenter2", null), "[Image] Content 2"),
                    )
                },
                Modifier.fillMaxWidth(),
            ) {
//                Text("""Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc dignissim facilisis dui, vitae suscipit velit molestie in. Sed at finibus sem. Vestibulum nibh nunc, blandit sit amet semper eget, varius at enim. Suspendisse porta blandit est, semper tincidunt nunc porta et. Suspendisse consequat quam eu dui mattis mollis. Donec est orci, luctus sit amet iaculis ut, convallis ac libero. Quisque porttitor commodo lorem ac sagittis. Aliquam lobortis leo nisi, at rhoncus felis molestie viverra. Pellentesque accumsan tincidunt molestie. Vivamus non ligula rhoncus, ultricies libero ac, feugiat nisl. Cras quis convallis nunc. Mauris at est in ante consequat venenatis.""".trimIndent(),)
                var actualHeight by remember { mutableStateOf(0.dp) }
                RichText(
                    """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc dignissim facilisis dui, vitae suscipit velit molestie in. Sed at finibus sem. Vestibulum nibh nunc, blandit sit amet semper eget, varius at enim. Suspendisse porta blandit est, semper tincidunt nunc porta et. Suspendisse consequat quam eu dui mattis mollis. Donec est orci, luctus sit amet iaculis ut, convallis ac libero. Quisque porttitor commodo lorem ac sagittis. Aliquam lobortis leo nisi, at rhoncus felis molestie viverra. Pellentesque accumsan tincidunt molestie. Vivamus non ligula rhoncus, ultricies libero ac, feugiat nisl. Cras quis convallis nunc. Mauris at est in ante consequat venenatis.""".trimIndent(),
                    modifier = Modifier.height(actualHeight).fillMaxWidth(),
                    propagateScrollState = scrollState,
                    onActualContentSizeChange = {
                        actualHeight = it.height
                    }
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

