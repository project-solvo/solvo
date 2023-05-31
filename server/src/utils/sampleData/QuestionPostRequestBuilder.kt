package org.solvo.server.utils.sampleData

class QuestionPostRequest(
    val code: String,
    val content: String,
    val anonymity: Boolean,
)

class QuestionPostRequestBuilder(
    private val code: String
) {
    private var content = ""
    private var anonymity = false

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

    fun build(): QuestionPostRequest {
        return QuestionPostRequest(code, content, anonymity)
    }
}