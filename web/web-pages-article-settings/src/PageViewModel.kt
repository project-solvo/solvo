package org.solvo.web.pages.article.settings

import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.communication.QuestionEditRequest
import org.solvo.model.api.communication.isEmpty
import org.solvo.model.utils.nonBlankOrNull
import org.solvo.web.document.History
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.article
import org.solvo.web.document.parameters.question
import org.solvo.web.pages.article.settings.groups.QuestionSettingGroup
import org.solvo.web.requests.client
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground
import kotlin.time.Duration.Companion.seconds

@Stable
interface PageViewModel {
    val pathParameters: PathParameters

    val courseCode: StateFlow<String>
    val articleCode: StateFlow<String>
    val questionCode: StateFlow<String?>
    val article: SharedFlow<ArticleDownstream>

    val questionGroups: StateFlow<List<QuestionSettingGroup>?>
    val selectedQuestionGroup: StateFlow<QuestionSettingGroup?>
}

@JsName("createPageViewModel")
fun PageViewModel(): PageViewModel = PageViewModelImpl()

@Stable
private class PageViewModelImpl : AbstractViewModel(), PageViewModel {
    override val pathParameters: PathParameters = PathParameters(WebPagePathPatterns.articleSettingsQuestion)

    override val courseCode: StateFlow<String> = pathParameters.argument(WebPagePathPatterns.VAR_COURSE_CODE)
    override val articleCode: StateFlow<String> = pathParameters.argument(WebPagePathPatterns.VAR_ARTICLE_CODE)
    override val questionCode: StateFlow<String?> =
        pathParameters.argumentNullable(WebPagePathPatterns.VAR_QUESTION_CODE)

    override val article = pathParameters.article().filterNotNull().shareInBackground()

    val questionsIndexes = article.map { it.questionIndexes }.stateInBackground(emptyList())

    override val questionGroups = questionsIndexes.mapLatest { list ->
        list.map { QuestionSettingGroup(it) }
    }.stateInBackground()

    override val selectedQuestionGroup: StateFlow<QuestionSettingGroup?> = combine(
        questionGroups,
        questionCode,
    ) { list, code ->
        list?.find { it.pathName == code }
    }.stateInBackground()
}


@Stable
interface QuestionViewModel : PageViewModel {
    val question: StateFlow<QuestionDownstream?>

    val newCode: StateFlow<String>
    fun setNewCode(value: String)

    val isNewCodeAvailable: StateFlow<Boolean?> // null: loading
    val newCodeError: StateFlow<String?>

//    val content: StateFlow<String>

    fun openQuestionPage() {
        History.navigate {
            question(
                courseCode.value,
                articleCode.value,
                questionCode.value ?: return
            )
        }
    }


    fun submitBasicChanges(snackbar: SolvoSnackbar)
    fun submitContentChanges(
        snackbar: SolvoSnackbar,
        editorContent: String?
    )
}


@JsName("createQuestionViewModel")
fun QuestionViewModel(
    page: PageViewModel,
): QuestionViewModel = QuestionViewModelImpl(page)

@Stable
class QuestionViewModelImpl(
    private val page: PageViewModel,
) : QuestionViewModel, PageViewModel by page, AbstractViewModel() {
    override val question: StateFlow<QuestionDownstream?> =
        page.pathParameters.question().filterNotNull().stateInBackground()

    override val newCode: MutableStateFlow<String> = MutableStateFlow("")
    override fun setNewCode(value: String) {
        newCode.value = value.trim()
    }

    override val isNewCodeAvailable: StateFlow<Boolean?> = newCode.debounce(1.seconds).flatMapLatest { code ->
        if (code == question.value?.code) flowOf(true)
        else {
            deferFlowInBackground {
                !client.questions.isQuestionExist(courseCode.value, articleCode.value, code)
            }
        }
    }.stateInBackground(true)

    override val newCodeError: StateFlow<String?> = combine(isNewCodeAvailable) { (isNewCodeAvailable) ->
        when {
            isNewCodeAvailable == false -> "Question code already exist"
            else -> null
        }
    }.stateInBackground()

    override fun submitBasicChanges(snackbar: SolvoSnackbar) {
        launchInBackground {
            submitChange(
                QuestionEditRequest(
                    code = newCode.value.nonBlankOrNull
                )
            )
        }
    }

    private suspend fun submitChange(req: QuestionEditRequest) {
        if (req.isEmpty()) {
            return
        }
        questionCode.value?.let {
            client.questions.updateQuestion(
                courseCode.value, articleCode.value, it,
                req
            )
        }
    }

    override fun init() {
        launchInBackground {
            question.filterNotNull().collect {
                newCode.value = it.code
            }
        }
    }

    override fun submitContentChanges(snackbar: SolvoSnackbar, editorContent: String?) {
        launchInBackground {
            submitChange(
                QuestionEditRequest(
                    content = editorContent?.nonBlankOrNull,
                )
            )
            snackbar.showSnackbar("Changes saved")
        }
    }
}