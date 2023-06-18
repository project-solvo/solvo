package org.solvo.web.pages.article.settings.groups

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.ArticleEditRequest
import org.solvo.model.api.communication.isEmpty
import org.solvo.model.utils.nonBlankOrNull
import org.solvo.web.requests.client
import org.solvo.web.settings.components.AutoCheckProperty
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground

interface ArticlePropertiesViewModel {
    val newCode: AutoCheckProperty<String, String>
    val newDisplayName: AutoCheckProperty<String, String>

    fun submitBasicChanges(snackbar: SolvoSnackbar)
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
    private val originalCode = originalArticle.mapNotNull { it?.code }.stateInBackground("")
    private val originalDisplayName = originalArticle.mapNotNull { it?.displayName }.stateInBackground("")


    override val newDisplayName: AutoCheckProperty<String, String> = AutoCheckProperty(
        originalDisplayName,
    ) { code ->
        when {
            code == originalArticle.value?.displayName -> null
            else -> null
        }
    }

    override val newCode: AutoCheckProperty<String, String> = AutoCheckProperty(
        merge(originalCode, newDisplayName.valueFlow.map { it.lowercase().replace(' ', '_') }).stateInBackground(""),
    ) { code ->
        when {
            code == originalArticle.value?.code -> null
            client.articles.isArticleExist(courseCode.value, code) -> "Article code already exist"

            else -> null
        }
    }

    override fun submitBasicChanges(snackbar: SolvoSnackbar) {
        launchInBackground {
            var targetArticleCode = originalArticle.value?.code
            if (targetArticleCode == null) {
                targetArticleCode = newCode.value
                client.articles.addArticle(courseCode.value, newCode.value)
            }

            val request = ArticleEditRequest(
                code = newCode.value.nonBlankOrNull?.takeIf { it.str != originalCode.value },
                displayName = newDisplayName.value.nonBlankOrNull?.takeIf { it.str != originalDisplayName.value },
            )

            if (!request.isEmpty()) {
                client.articles.update(
                    courseCode.value, targetArticleCode, request
                )
            }
        }
    }
}