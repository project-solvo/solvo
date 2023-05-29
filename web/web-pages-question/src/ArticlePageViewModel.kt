package org.solvo.web

import androidx.compose.runtime.*
import org.solvo.model.ArticleDownstream
import org.solvo.web.viewModel.AbstractViewModel

@Stable
class ArticlePageViewModel : AbstractViewModel() {
    val allArticles: List<ArticleDownstream> = mutableStateListOf()

}