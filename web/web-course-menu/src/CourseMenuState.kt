package org.solvo.web.comments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.solvo.model.ArticleDownstream
import org.solvo.model.QuestionDownstream


@Stable
class CourseMenuState {

    private var _questionIndex: MutableState<QuestionDownstream?> = mutableStateOf(null)
    val questionIndex: State<QuestionDownstream?> get() = _questionIndex
    private val _menuOpen: MutableState<Boolean> = mutableStateOf(false)
    val menuOpen: State<Boolean> get() = _menuOpen
    private var _clickedArticle: MutableState<ArticleDownstream?> = mutableStateOf(null)
    val clickedArticle: State<ArticleDownstream?> get() = _clickedArticle

    private val _questionListOpen: MutableState<Boolean> = mutableStateOf(false)
    val questionListOpen: State<Boolean> get() = _questionListOpen
    fun switchMenuOpen() {
        _menuOpen.value = !_menuOpen.value
    }

    fun onClickArticle(article: ArticleDownstream) {
        if (_clickedArticle.value == article) {
            _questionListOpen.value = !_questionListOpen.value
        } else {
            _questionListOpen.value = true
            _clickedArticle.value = article
        }
    }

    fun onClickQuestion(article: ArticleDownstream, question: QuestionDownstream) {
        _questionIndex.value = question
        _clickedArticle.value = article
    }
}