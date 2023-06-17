package org.solvo.web.pages.article.settings.groups

import kotlinx.coroutines.flow.StateFlow
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.web.requests.client
import org.solvo.web.settings.components.AutoCheckProperty
import org.solvo.web.viewModel.AbstractViewModel

interface ArticlePropertiesViewModel {
    val newCode: AutoCheckProperty<String, String>
    val newDisplayName: AutoCheckProperty<String, String>
}

@JsName("createArticlePropertiesViewModel")
fun ArticlePropertiesViewModel(
    courseCode: StateFlow<String>,
    originalArticle: StateFlow<ArticleDownstream?>,
): ArticlePropertiesViewModel =
    ArticlePropertiesViewModelImpl(courseCode, originalArticle)

class ArticlePropertiesViewModelImpl(
    private val courseCode: StateFlow<String>,
    private val originalArticle: StateFlow<ArticleDownstream?>,
) : ArticlePropertiesViewModel, AbstractViewModel() {
    override val newCode: AutoCheckProperty<String, String> = AutoCheckProperty(
        "",
        transformValue = { it.trim() }
    ) { code ->
        when {
            code == originalArticle.value?.code -> null
            !client.articles.isArticleExist(
                courseCode.value,
                code
            ) -> "Article code already exist"

            else -> null
        }
    }

    override val newDisplayName: AutoCheckProperty<String, String> = AutoCheckProperty(
        "",
        transformValue = { it.trim() }
    ) { code ->
        when {
            code == originalArticle.value?.displayName -> null
            else -> null
        }
    }
}