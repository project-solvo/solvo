package org.solvo.web

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.*
import org.solvo.model.api.events.CommentEvent
import org.solvo.model.api.events.Event
import org.solvo.web.comments.CommentEventHandler
import org.solvo.web.comments.CourseMenuState
import org.solvo.web.document.parameters.*
import org.solvo.web.editor.RichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.snackBar.SnackbarTheme
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.LoadingUuidItem

@Stable
interface QuestionPageViewModel {
    val events: SharedFlow<Event>

    val course: StateFlow<Course?>
    val article: StateFlow<ArticleDownstream?>
    val question: StateFlow<QuestionDownstream?>

    val draftEditorState: RichEditorState

    val allAnswers: StateFlow<List<LoadingUuidItem<CommentDownstream>>>
    val isSingleAnswer: StateFlow<Boolean>

    val expandedIndex: StateFlow<Int?>
    val expandedAnswer: StateFlow<LoadingUuidItem<CommentDownstream>?>
    val expandedAnswerReady: StateFlow<CommentDownstream?>
    val isExpanded: StateFlow<Boolean>

    val backgroundScope: CoroutineScope
    val menuState: CourseMenuState

    val controlBarState: DraftAnswerControlBarState


    fun expandAnswer(answerIndex: Int)

    fun startEditingAnswer(item: CommentDownstream)
    fun askDeleteAnswer(item: CommentDownstream, snackbar: SolvoSnackbar)

    fun submitComment(newComment: CommentUpstream)
    fun collapse()
}

fun QuestionPageViewModel.expandAnswer(answer: CommentDownstream) {
    val index = this.allAnswers.value.indexOfFirst { answer.coid == it.coid }
    if (index == -1) {
        return
    }
    return expandAnswer(index)
}

@JsName("createQuestionPageViewModel")
fun QuestionPageViewModel(
    draftAnswerEditor: RichEditorState
): QuestionPageViewModel = QuestionPageViewModelImpl(draftAnswerEditor)

@Stable
class QuestionPageViewModelImpl internal constructor(
    override val draftEditorState: RichEditorState
) : AbstractViewModel(), QuestionPageViewModel {
    private val pathParameters = PathParameters(WebPagePathPatterns.question)
    override val events: SharedFlow<Event> =
        pathParameters.questionEvents(backgroundScope).shareInBackground()

    override val course = pathParameters.course().stateInBackground()
    override val article = pathParameters.article().stateInBackground()
    override val question = pathParameters.question().stateInBackground()

    private val eventHandler = CommentEventHandler(
        getCurrentAllComments = { allAnswers.value }
    )

    private val newAnswers = events
        .filterIsInstance<CommentEvent>()
        .filter { event ->
            event.parentCoid == question.value?.coid // new answer
                    || allAnswers.value.any { it.coid == event.commentCoid } // update answer (e.g. update previews)
        }
        .map { event ->
            eventHandler.handleEvent(event)
        }

    private val remoteAnswers = question.filterNotNull().mapLatestSupervised { question ->
        question.answers
            .mapLoadIn(backgroundScope) { client.comments.getComment(it) }
    }

    override val allAnswers: StateFlow<List<LoadingUuidItem<CommentDownstream>>> =
        merge(remoteAnswers, newAnswers).stateInBackground(emptyList())
    override val isSingleAnswer: StateFlow<Boolean> = allAnswers.map { it.size == 1 }.stateInBackground(false)

    override val expandedIndex: MutableStateFlow<Int?> = MutableStateFlow(null)

    override val expandedAnswer: StateFlow<LoadingUuidItem<CommentDownstream>?> =
        combine(expandedIndex, allAnswers) { index, list ->
            if (index == null) {
                null
            } else {
                list.getOrNull(index)
            }
        }.stateInBackground()

    override val expandedAnswerReady: StateFlow<CommentDownstream?> =
        expandedAnswer.filterNotNull().flatMapLatest { it.asFlow() }.stateInBackground()

    override val isExpanded: StateFlow<Boolean> = expandedAnswer.map { it != null }.stateInBackground(false)

    override val controlBarState: DraftAnswerControlBarState = DraftAnswerControlBarState()
    override fun expandAnswer(answerIndex: Int) {
        println("expandAnswer: $answerIndex")
        expandedIndex.value = answerIndex
    }

    override fun startEditingAnswer(item: CommentDownstream) {
        backgroundScope.launch {
            draftEditorState.setContentMarkdown(item.content)
            controlBarState.startDraft(DraftKind.Edit(item))
        }
    }

    override fun askDeleteAnswer(item: CommentDownstream, snackbar: SolvoSnackbar) {
        backgroundScope.launch {
            val res = snackbar.showSnackbar(
                "Are you sure to delete this? Deletion can not be revoked!",
                actionLabel = "Delete",
                withDismissAction = true,
                theme = SnackbarTheme(
                    actionColor = Color.Red
                ),
            )
            if (res == SnackbarResult.ActionPerformed) {
                client.comments.deleteComment(item.coid)
            }
        }
    }

    override fun submitComment(newComment: CommentUpstream) {
        val comment = expandedAnswerReady.value ?: return
        backgroundScope.launch {
            client.comments.post(comment.coid, newComment, CommentKind.COMMENT)
        }
    }

    override fun collapse() {
        expandedIndex.value = null
    }

//    val allArticles: SharedFlow<List<ArticleDownstream>> = course.filterNotNull().mapNotNull {
//        client.courses.getAllArticles(it.code)
//    }.onEach {
//        menuState.setArticles(it)
//    }.shareInBackground()

    override val menuState = CourseMenuState()
}