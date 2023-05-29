package org.solvo.web

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import org.solvo.model.Article
import org.solvo.web.viewModel.AbstractViewModel

@Stable
class ArticlePageViewModel : AbstractViewModel() {
    val allArticles: List<Article> = mutableStateListOf()
}