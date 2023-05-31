package org.solvo.server.utils.sampleData

import org.solvo.model.ArticleUpstream
import org.solvo.model.QuestionUpstream

@SampleDataDslMarker
class ArticlePostRequestBuilder {
    private var content: String = ""
    private var anonymity: Boolean = false
    private var code: String = ""
    private var displayName: String = ""
    private var termYear: String = ""
    private var questions: List<String> = listOf()
    private var author: UserRegisterRequest? = null

    fun content(set: () -> String) {
        content = set()
    }

    fun anonymity(set: () -> Boolean) {
        anonymity = set()
    }

    fun code(set: () -> String) {
        code = set()
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

    fun author(set: () -> UserRegisterRequest) {
        author = set()
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
        author = author!!,
    )
}