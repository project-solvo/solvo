package org.solvo.web.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Article
import org.solvo.model.Question
import org.solvo.web.document.SolvoWindow
import org.solvo.web.ui.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            CoursePageContent()
        }
    }
}

@Composable
fun CoursePageContent() {
    var menuOpen by remember { mutableStateOf(false) }
    var clickedYear by remember { mutableStateOf(-1) }
    SolvoTopAppBar {
        IconButton(onClick = { menuOpen = !menuOpen }) {
            Icon(Icons.Filled.Menu, null)
        }
    }
    Box {
        Column(
            modifier = Modifier.fillMaxSize().padding(100.dp).verticalScroll(rememberScrollState())
        ) {
            // content

        }
        // show left column menu
    }
}


@Composable
fun CourseMenu(
    isOpen: Boolean,
    articles: MutableList<Article>,
    onClickArticle: (article: Article) -> Unit,
    onClickQuestion: (article: Article, question: Question) -> Unit,
) {
    AnimatedVisibility(
        isOpen,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .width(250.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(10.dp))
        ) {
            // construct list of past papers
            articles.forEachIndexed { index, year ->
                var questionListOpen by remember { mutableStateOf(false) }

                ElevatedCard(
                    onClick = {
                        questionListOpen = !questionListOpen
                    },
                    modifier = Modifier.padding(10.dp).width(200.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = year.termYear,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    AnimatedVisibility(questionListOpen) {
                        // construct list of questions
                        var checkedIndex: Int by remember { mutableStateOf(-1) }

                        Column(Modifier.wrapContentHeight()) {
                            year.questions.forEachIndexed { i, question ->
                                Row(
                                    modifier = Modifier.padding(10.dp).padding(start = 20.dp)
                                        .height(60.dp).width(160.dp).clickable {
                                            checkedIndex = i
                                            onClickArticle(year)
//                                            clickedYear = index
                                        }
                                        .background(
                                            color = if (checkedIndex == i
//                                                && index == clickedYear
                                            ) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(8.dp)

                                        ),
                                ) {
                                    Text(
                                        text = question.content,
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}