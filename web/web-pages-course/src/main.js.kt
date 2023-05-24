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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Artical
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
    Box {
        Column(
            modifier = Modifier.fillMaxSize().padding(100.dp).verticalScroll(rememberScrollState())
        ) {

        }
    }
    Box {
        var menuOpen by remember { mutableStateOf(true) }
        SolvoTopAppBar {
            IconButton(onClick = { menuOpen = !menuOpen }) {
                Icon(Icons.Filled.Menu, null)
            }
        }
        // show left column menu
        AnimatedVisibility(
            menuOpen,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Column(
                modifier = Modifier.offset(0.dp, 60.dp).verticalScroll(rememberScrollState())
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
        modifier = Modifier.padding(10.dp).width(200.dp).clickable {},
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
    Column {
        // record the current selected question
        val clickedList = remember {
            mutableStateListOf<Boolean>().apply {
                questions.forEach { _ ->
                    this.add(false)
                }
            }
        }
        questions.forEachIndexed { index, question ->
            ElevatedCard(
                onClick = {
                    // change the state of the record list.
                    clickedList.forEachIndexed { i, _ ->
                        clickedList[i] = i == index
                    }
                },
                modifier = Modifier.padding(10.dp).height(60.dp).width(160.dp).offset(20.dp).clickable {},
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    // change color when the question is selected
                    containerColor = if (clickedList[index]) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                )
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
