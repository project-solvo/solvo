package org.solvo.web.pages.article.settings

import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.article
import org.solvo.web.pages.article.settings.groups.ArticleSettingGroup
import org.solvo.web.pages.article.settings.groups.QuestionSettingGroup
import org.solvo.web.viewModel.AbstractViewModel

@Stable
interface PageViewModel {
    val pathParameters: PathParameters

    val courseCode: StateFlow<String>
    val articleCode: StateFlow<String>
    val questionCode: StateFlow<String?>
    val article: SharedFlow<ArticleDownstream>

    val settingGroups: StateFlow<List<ArticleSettingGroup>?>
    val selectedSettingGroup: StateFlow<ArticleSettingGroup?>
}

@JsName("createPageViewModel")
fun PageViewModel(): PageViewModel = PageViewModelImpl()

@Stable
private class PageViewModelImpl : AbstractViewModel(), PageViewModel {
    override val pathParameters: PathParameters = PathParameters(WebPagePathPatterns.articleSettingsGroup)

    override val courseCode: StateFlow<String> = pathParameters.argument(WebPagePathPatterns.VAR_COURSE_CODE)
    override val articleCode: StateFlow<String> = pathParameters.argument(WebPagePathPatterns.VAR_ARTICLE_CODE)
    override val questionCode: StateFlow<String?> =
        pathParameters.argumentNullable(WebPagePathPatterns.VAR_QUESTION_CODE)

    override val article = pathParameters.article().filterNotNull().shareInBackground()

    val questionsIndexes = article.map { it.questionIndexes }.stateInBackground(emptyList())

    override val settingGroups = questionsIndexes.mapLatest { list ->
        ArticleSettingGroup.articleSettingGroups + list.map { QuestionSettingGroup(it) }
    }.stateInBackground()

    override val selectedSettingGroup: StateFlow<ArticleSettingGroup?> = combine(
        settingGroups,
        questionCode,
    ) { list, code ->
        list?.find { it.pathName == code }
    }.stateInBackground()
}
