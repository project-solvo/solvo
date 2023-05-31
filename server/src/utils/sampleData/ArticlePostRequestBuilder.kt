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

    @PublishedApi
    internal var questions: MutableList<QuestionPostRequest> = mutableListOf()

    fun content(set: () -> String) {
        content = set()
    }

    fun content(content: String) {
        this.content = content
    }

    fun anonymity(set: () -> Boolean) {
        anonymity = set()
    }

    fun anonymity(anonymity: Boolean) {
        this.anonymity = anonymity
    }

    fun anonymous() {
        anonymity = true
    }

    fun displayName(set: () -> String) {
        displayName = set()
    }

    fun displayName(displayName: String) {
        this.displayName = displayName
    }

    fun termYear(set: () -> String) {
        termYear = set()
    }

    fun termYear(termYear: String) {
        this.termYear = termYear
    }

    fun questions(set: () -> List<String>) {
        questions = set().map { QuestionPostRequest(it, "Haha", true) }.toMutableList()
    }

    fun questions(questions: List<String>) {
        this.questions = questions.map { QuestionPostRequest(it, "Haha", true) }.toMutableList()
    }

    @SampleDataDslMarker
    inline fun question(code: String, builds: QuestionPostRequestBuilder.() -> Unit) {
        questions.add(QuestionPostRequestBuilder(code).apply(builds).build())
    }

    fun build(): ArticlePostRequest = ArticlePostRequest(
        article = ArticleUpstream(
            content = content,
            anonymity = anonymity,
            code = code,
            displayName = displayName,
            termYear = termYear,
            questions = questions.map { QuestionUpstream(it.content, it.anonymity, it.code) }
        ),
        author = author,
    )
}