package org.solvo.web

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.*
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.web.comments.CourseMenuState
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.article
import org.solvo.web.document.parameters.course
import org.solvo.web.document.parameters.question
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

@Stable
class QuestionPageViewModel : AbstractViewModel() {
    private val pathParameters = PathParameters(WebPagePathPatterns.question)

    val course = pathParameters.course().shareInBackground()
    val article = pathParameters.article().shareInBackground()
    val question = pathParameters.question().shareInBackground()

    val allAnswers = question.filterNotNull().map { question ->
        question.answers.asFlow()
            .mapNotNull { client.comments.getComment(it) }
            .filterNot { it.content.isBlank() }
            .runningList()
    }.shareInBackground()


//    val allArticles: SharedFlow<List<ArticleDownstream>> = course.filterNotNull().mapNotNull {
//        client.courses.getAllArticles(it.code)
//    }.onEach {
//        menuState.setArticles(it)
//    }.shareInBackground()

    val menuState = CourseMenuState()
}