package org.solvo.web.pages.article.settings.groups

import kotlinx.coroutines.flow.*
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import kotlin.time.Duration.Companion.seconds

interface ArticlePropertiesViewModel : PageViewModel {
    val newCode: StateFlow<String>
    fun setNewCode(value: String)
    val isNewCodeAvailable: StateFlow<Boolean?> // null: loading
    val newCodeError: StateFlow<String?>
}

@JsName("createArticlePropertiesViewModel")
fun ArticlePropertiesViewModel(viewModel: PageViewModel): ArticlePropertiesViewModel =
    ArticlePropertiesViewModelImpl(viewModel)

class ArticlePropertiesViewModelImpl(
    private val page: PageViewModel
) : ArticlePropertiesViewModel, PageViewModel by page, AbstractViewModel() {
    override val newCode: MutableStateFlow<String> = MutableStateFlow("")
    override fun setNewCode(value: String) {
        newCode.value = value.trim()
    }

    override val isNewCodeAvailable: StateFlow<Boolean?> = newCode.debounce(1.seconds).flatMapLatest { code ->
        if (code == articleCode.value) flowOf(true)
        else {
            deferFlowInBackground {
                !client.articles.isArticleExist(courseCode.value, articleCode.value)
            }
        }
    }.stateInBackground(true)

    override val newCodeError: StateFlow<String?> = combine(isNewCodeAvailable) { (isNewCodeAvailable) ->
        when {
            isNewCodeAvailable == false -> "Article code already exist"
            else -> null
        }
    }.stateInBackground()

}