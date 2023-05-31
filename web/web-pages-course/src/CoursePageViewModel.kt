package org.solvo.web

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.solvo.model.ArticleDownstream
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

class CoursePageViewModel: AbstractViewModel() {
    val articles: MutableState<List<ArticleDownstream>?> = mutableStateOf(null)
    val courseCode: MutableState<String?> = mutableStateOf(null)

    suspend fun refreshArticles(code: String?) {
        courseCode.value = code
        val courseCode = this.courseCode.value ?: return
        val articles = client.courses.getAllArticles(courseCode)
        this.articles.value = articles
    }
}