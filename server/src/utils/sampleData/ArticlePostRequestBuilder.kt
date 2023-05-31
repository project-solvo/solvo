package org.solvo.server.utils.sampleData

import org.solvo.model.ArticleUpstream
import org.solvo.model.QuestionUpstream

class ArticlePostRequest(
    val article: ArticleUpstream,
    val author: UserRegisterRequest,
)

@SampleDataDslMarker
class ArticlePostRequestBuilder(
    private val code: String,
    private val author: UserRegisterRequest,
) {
    private var content: String = ""
    private var anonymity: Boolean = false
    private var displayName: String = code
    private var termYear: String = ""
    private var questions: List<String> = listOf()

    fun content(set: () -> String) {
        content = set()
    }

    fun anonymity(set: () -> Boolean) {
        anonymity = set()
    }

    fun displayName(set: () -> String) {
        displayName = set()
    }

    fun termYear(set: () -> String) {
        termYear = set()
    }

    fun questions(set: () -> List<String>) {
        questions = set()
    }

    fun build(): ArticlePostRequest = ArticlePostRequest(
        article = ArticleUpstream(
            content = content,
            anonymity = anonymity,
            code = code,
            displayName = displayName,
            termYear = termYear,
            questions = questions.map { QuestionUpstream("Haha", true, it) },
        ),
        author = author,
    )
}