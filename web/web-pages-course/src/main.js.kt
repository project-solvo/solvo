package org.solvo.web

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
import org.solvo.model.Artical
import org.solvo.model.Question
import org.solvo.web.document.SolvoWindow
import org.solvo.web.document.WindowState
import org.solvo.web.ui.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            CoursePageContent(this)
        }
    }
}

@Composable
fun CoursePageContent(windowState: WindowState) {
    var menuOpen by remember { mutableStateOf(false) }
    SolvoTopAppBar(windowState) {
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
        AnimatedVisibility(
            menuOpen,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(10.dp))
            ) {
                val years = remember {
                    mutableListOf<Artical>().apply {
                        for (i in 2018 until 2023) {
                            val questions =
                                listOf(
                                    Question("1a"), Question("1b"),
                                    Question("2a"), Question("2b")
                                )
                            this.add(Artical(i.toString(), questions))
                        }
                    }
                }
                for (courseName in years) {
                    // construct list of past papers
                    PastPaperCard(courseName)
                }
            }
        }
    }
}

@Composable
private fun PastPaperCard(item: Artical) {
    var questionListOpen by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = {
            questionListOpen = !questionListOpen
        },
        modifier = Modifier.padding(10.dp).width(200.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = item.year,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleLarge,
        )

        AnimatedVisibility(questionListOpen) {
            // construct list of questions
            QuestionCards(item.questions)
        }
    }
}

@Composable
fun QuestionCards(questions: List<Question>) {
    var checkedIndex: Int by remember { mutableStateOf(-1) }

    Column(Modifier.wrapContentHeight()) {
        questions.forEachIndexed { index, question ->
            Row(
                modifier = Modifier.padding(10.dp).padding(start = 20.dp)
                    .height(60.dp).width(160.dp).clickable { checkedIndex = index }
                    .background(
                        color = if (checkedIndex == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ),
            ) {
                Text(
                    text = question.qid,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
