package org.solvo.web

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import org.solvo.model.ArticleDownstream
import org.solvo.web.viewModel.AbstractViewModel

@Stable
class ArticlePageViewModel : AbstractViewModel() {
    val allArticles: List<ArticleDownstream> = mutableStateListOf()
}