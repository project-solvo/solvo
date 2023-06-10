package org.solvo.web

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.*
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.events.CommentEvent
import org.solvo.web.comments.CommentEventHandler
import org.solvo.web.comments.CourseMenuState
import org.solvo.web.document.parameters.*
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.LoadingUuidItem

@Stable
class QuestionPageViewModel : AbstractViewModel() {
    private val pathParameters = PathParameters(WebPagePathPatterns.question)

    val course = pathParameters.course().shareInBackground()
    val article = pathParameters.article().shareInBackground()
    val question = pathParameters.question().stateInBackground()

    val questionEvents = pathParameters.questionEvents(backgroundScope).flatMapLatest { it }.shareInBackground()

    private val eventHandler = CommentEventHandler(
        getCurrentAllComments = { allAnswers.value }
    )

    private val newAnswers = questionEvents
        .filterIsInstance<CommentEvent>()
        .filter { it.parentCoid == question.value?.coid }
        .map { event ->
            eventHandler.handleEvent(event)
        }

    private val remoteAnswers = question.filterNotNull().mapLatestSupervised { question ->
        question.answers
            .mapLoadIn(backgroundScope) { client.comments.getComment(it) }
    }

    val allAnswers: StateFlow<List<LoadingUuidItem<CommentDownstream>>> =
        merge(remoteAnswers, newAnswers).stateInBackground(emptyList())


//    val allArticles: SharedFlow<List<ArticleDownstream>> = course.filterNotNull().mapNotNull {
//        client.courses.getAllArticles(it.code)
//    }.onEach {
//        menuState.setArticles(it)
//    }.shareInBackground()

    val menuState = CourseMenuState()
}