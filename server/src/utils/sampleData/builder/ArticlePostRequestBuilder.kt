package org.solvo.server.utils.sampleData.builder

import org.intellij.lang.annotations.Language
import org.solvo.model.api.communication.ArticleEditRequest
import org.solvo.model.utils.NonBlankString
import org.solvo.server.ServerContext
import org.solvo.server.utils.sampleData.SampleDataDslMarker

class ArticlePostRequest(
    val content: () -> String,
    val anonymity: Boolean,
    val code: String,
    val displayName: String,
    val termYear: String,
    val author: UserRegisterRequest,
    val questions: List<QuestionPostRequest>,
    val comments: List<CommentPostRequest> = listOf(),
) {
    suspend fun submit(
        db: ServerContext.Databases,
        courseCode: String
    ) {
        db.contents.apply {
            val articleId = createArticle(
                articleCode = NonBlankString.fromString(code),
                authorId = author.uid,
                courseCode = courseCode,
            )!!
            editArticle(
                request = ArticleEditRequest(
                    content = NonBlankString.fromString(content()),
                    anonymity = anonymity,
                    displayName = NonBlankString.fromString(displayName),
                ),
                userId = author.uid,
                articleId = articleId,
            )
            questions.map { questionRequest ->
                questionRequest.submit(
                    db,
                    articleId,
                    author
                )
            }
            comments.map { commentRequest -> commentRequest.submit(db, articleId) }
        }
    }
}

@SampleDataDslMarker
class ArticlePostRequestBuilder(
    private val code: String,
    private val author: UserRegisterRequest,
) {
    private var content: () -> String = { "Default content" }
    private var anonymity: Boolean = false
    private var displayName: String = code
    private var termYear: String = "Default term year"

    @PublishedApi
    internal var questions: MutableList<QuestionPostRequest> = mutableListOf()

    @PublishedApi
    internal var comments: MutableList<CommentPostRequest> = mutableListOf()

    fun content(set: () -> String) {
        content = set
    }

    fun content(@Language("md") content: String) {
        this.content = { content }
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

    fun defaultQuestions(set: () -> List<String>) {
        questions = set().map { QuestionPostRequest(it, { "Haha" }, true) }.toMutableList()
    }

    fun defaultQuestions(questions: List<String>) {
        this.questions = questions.map { QuestionPostRequest(it, { "Haha" }, true) }.toMutableList()
    }

    @SampleDataDslMarker
    inline fun question(code: String, builds: QuestionPostRequestBuilder.() -> Unit) {
        questions.add(QuestionPostRequestBuilder(code).apply(builds).build())
    }

    @SampleDataDslMarker
    inline fun comment(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author).apply(builds).build())
    }

    fun build(): ArticlePostRequest = ArticlePostRequest(
        content = content,
        anonymity = anonymity,
        code = code,
        displayName = displayName,
        termYear = termYear,
        author = author,
        questions = questions,
        comments = comments,
    )
}