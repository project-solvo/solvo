package org.solvo.web

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.solvo.model.ArticleDownstream
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

    val allArticles: SharedFlow<List<ArticleDownstream>> = course.filterNotNull().mapNotNull {
        client.courses.getAllArticles(it.code)
    }.onEach {
        menuState.setArticles(it)
    }.shareInBackground()

    val menuState = CourseMenuState()
}