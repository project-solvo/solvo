package org.solvo.web.comments

import androidx.compose.runtime.*
import org.solvo.model.Article
import org.solvo.model.Question


@Stable
class CourseState {

    private var _questionIndex: MutableState<Question?> = mutableStateOf(null)
    val questionIndex: State<Question?> get() = _questionIndex
    private val _menuOpen: MutableState<Boolean> = mutableStateOf(false)
    val menuOpen: State<Boolean> get() = _menuOpen
    private var _clickedArticle: MutableState<Article?> = mutableStateOf(null)
    val clickedArticle: State<Article?> get() = _clickedArticle

    private val _questionListOpen: MutableState<Boolean> = mutableStateOf(false)
    val questionListOpen: State<Boolean> get() = _questionListOpen
    fun switchMenuOpen() {
        _menuOpen.value = !_menuOpen.value
    }

    fun onClickArticle(article: Article) {
        if (_clickedArticle.value == article) {
            _questionListOpen.value = !_questionListOpen.value
        } else {
            _questionListOpen.value = true
            _clickedArticle.value = article
        }
    }

    fun onClickQuestion(article: Article, question: Question) {
        _questionIndex.value = question
        _clickedArticle.value = article
    }
}