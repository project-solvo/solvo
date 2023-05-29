package org.solvo.web

import PagingViewModel
import androidx.compose.runtime.*
import org.solvo.model.ArticleDownstream

@Stable
class ArticlePageViewModel : PagingViewModel() {
    val allArticles: List<ArticleDownstream> = mutableStateListOf()
}