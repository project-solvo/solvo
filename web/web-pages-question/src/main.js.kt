package org.solvo.web

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(1) {
            // TODO: 2023/5/25 view model 
            CommentCard(
                listOf(
                    LightCommentDownstream(User(id = Uuid.random(), "查尔斯", null), "你是好人！"),
                    LightCommentDownstream(User(id = Uuid.random(), "Commenter2", null), "[Image] Content 2"),
                ),
                Modifier.fillMaxWidth().height(4000.dp),
            ) {
                RichText(
                    """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc dignissim facilisis dui, vitae suscipit velit molestie in. Sed at finibus sem. Vestibulum nibh nunc, blandit sit amet semper eget, varius at enim. Suspendisse porta blandit est, semper tincidunt nunc porta et. Suspendisse consequat quam eu dui mattis mollis. Donec est orci, luctus sit amet iaculis ut, convallis ac libero. Quisque porttitor commodo lorem ac sagittis. Aliquam lobortis leo nisi, at rhoncus felis molestie viverra. Pellentesque accumsan tincidunt molestie. Vivamus non ligula rhoncus, ultricies libero ac, feugiat nisl. Cras quis convallis nunc. Mauris at est in ante consequat venenatis.

Sed aliquam sapien in eros facilisis, at bibendum erat ornare. Proin faucibus iaculis ornare. Suspendisse sed nulla quam. Morbi id quam tellus. Duis ac bibendum dui. Etiam consectetur felis ac eros auctor, et rutrum sapien pulvinar. Donec at ex mollis, sodales mauris commodo, sodales dolor. Aenean non augue dui. Phasellus nec dictum ante. Maecenas nec ullamcorper turpis, sed gravida ipsum. Cras tempus, ex non maximus bibendum, odio ligula luctus nulla, non rhoncus magna urna ac diam. Sed eget purus efficitur, pulvinar elit in, sollicitudin mauris. Nunc sodales nibh eget lacus tincidunt, a iaculis augue maximus.

Proin sagittis tempor nisi id ornare. Fusce non ligula et augue hendrerit molestie. Nam in convallis felis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque iaculis nisl est, non iaculis massa hendrerit ac. Suspendisse suscipit feugiat nibh eget consectetur. Interdum et malesuada fames ac ante ipsum primis in faucibus.

Integer sed ullamcorper lacus. Curabitur scelerisque egestas tempor. Nulla ligula erat, mattis ac eros at, sollicitudin elementum augue. Suspendisse id congue diam, at iaculis nisl. Sed a maximus sapien. Aliquam sed ipsum magna. Duis rhoncus et diam vel finibus. In quis ultrices quam. Fusce consequat porttitor eros. Sed ac lectus in enim dapibus semper. Cras massa libero, hendrerit eu nibh eu, hendrerit sagittis orci. Aliquam eget pharetra velit. Ut et orci id ligula aliquet iaculis. Ut vel tincidunt nibh. Sed est nisl, congue ut molestie nec, auctor eget dolor.

Suspendisse vitae sapien lacus. Quisque commodo urna eget erat sodales, sit amet efficitur augue condimentum. Aenean purus nunc, ultricies eget interdum id, varius vel lacus. Integer vitae lectus eu leo fermentum ultrices id id arcu. Nullam varius lectus ut mattis ornare. Nam condimentum tincidunt est, ut pellentesque ligula tempus ullamcorper. Etiam convallis, quam viverra euismod placerat, eros nisi imperdiet felis, in vehicula augue nunc venenatis odio. Mauris lacinia justo tortor, sed sodales ligula sollicitudin in. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam aliquam interdum ligula, ac lacinia ante pellentesque sodales. Pellentesque et cursus erat, ut convallis lorem. Duis cursus enim ut quam tincidunt malesuada. Nunc sed orci eu mi rutrum condimentum. Mauris posuere efficitur risus at malesuada. Sed dignissim arcu enim, et laoreet libero aliquet eget.

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