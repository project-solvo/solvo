package org.solvo.web.pages.article.settings

import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.events.ArticleSettingPageEvent
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.article
import org.solvo.web.event.withEvents
import org.solvo.web.pages.article.settings.groups.ArticleSettingGroup
import org.solvo.web.pages.article.settings.groups.QuestionSettingGroup
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

@Stable
interface PageViewModel {
    val pathParameters: PathParameters
    val articlePageEvents: SharedFlow<ArticleSettingPageEvent>

    val courseCode: StateFlow<String>
    val articleCode: StateFlow<String>
    val settingGroupName: StateFlow<String?>
    val article: StateFlow<ArticleDownstream?>

    val settingGroups: StateFlow<List<ArticleSettingGroup>?>
    val selectedSettingGroup: StateFlow<ArticleSettingGroup?>

    val isFullscreen: StateFlow<Boolean>
    fun setFullscreen(isFullscreen: Boolean)
}

@JsName("createPageViewModel")
fun PageViewModel(): PageViewModel = PageViewModelImpl()

@Stable
private class PageViewModelImpl : AbstractViewModel(), PageViewModel {
    override val pathParameters: PathParameters = PathParameters(WebPagePathPatterns.articleSettingsGroup)

    override val courseCode: StateFlow<String> = pathParameters.argument(WebPagePathPatterns.VAR_COURSE_CODE)
    override val articleCode: StateFlow<String> = pathParameters.argument(WebPagePathPatterns.VAR_ARTICLE_CODE)
    override val settingGroupName: StateFlow<String?> =
        pathParameters.argumentNullable(WebPagePathPatterns.VAR_SETTING_GROUP)

    override val articlePageEvents: SharedFlow<ArticleSettingPageEvent> =
        combine(courseCode, articleCode) { courseCode, articleCode ->
            client.articles.subscribeEvents(backgroundScope, courseCode, articleCode)
        }.flatMapLatest { it }.shareInBackground()

    override val article =
        pathParameters.article().stateInBackground()
            .withEvents(articlePageEvents.filterIsInstance())
            .stateInBackground()

    val questionsIndexes = article.filterNotNull().map { it.questionIndexes }.stateInBackground(emptyList())

    override val settingGroups = questionsIndexes.mapLatest { list ->
        ArticleSettingGroup.articleSettingGroups + list.map { QuestionSettingGroup(it) } + ArticleSettingGroup.managementGroups
    }.stateInBackground()

    override val selectedSettingGroup: StateFlow<ArticleSettingGroup?> = combine(
        settingGroups,
        settingGroupName,
    ) { list, code ->
        list?.find { it.pathName == code }
    }.stateInBackground()

    override val isFullscreen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override fun setFullscreen(isFullscreen: Boolean) {
        this.isFullscreen.value = isFullscreen
    }
}
