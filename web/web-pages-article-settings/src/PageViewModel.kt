package org.solvo.web.pages.article.settings

import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.article
import org.solvo.web.document.parameters.question
import org.solvo.web.pages.article.settings.groups.QuestionSettingGroup
import org.solvo.web.viewModel.AbstractViewModel

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
private class PageViewModelImpl(
) : AbstractViewModel(), PageViewModel {
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
    val question: SharedFlow<QuestionDownstream>

//    val content: StateFlow<String>
}


@JsName("createQuestionViewModel")
fun QuestionViewModel(
    page: PageViewModel,
): QuestionViewModel = QuestionViewModelImpl(page)

@Stable
class QuestionViewModelImpl(
    private val page: PageViewModel,
) : QuestionViewModel, PageViewModel by page, AbstractViewModel() {
    override val question: SharedFlow<QuestionDownstream> =
        page.pathParameters.question().filterNotNull().shareInBackground()
}