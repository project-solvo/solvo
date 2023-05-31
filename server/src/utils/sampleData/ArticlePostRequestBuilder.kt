package org.solvo.server.utils.sampleData

import org.solvo.model.ArticleUpstream
import org.solvo.model.QuestionUpstream
import org.solvo.server.ServerContext
import java.util.*

class ArticlePostRequest(
    val article: ArticleUpstream,
    val author: UserRegisterRequest,
    val questions: List<QuestionPostRequest>,
    val comments: List<CommentPostRequest> = listOf(),
) {
    suspend fun submit(
        db: ServerContext.Databases,
        userIdMap: Map<UserRegisterRequest, UUID>,
        courseCode: String
    ) {
        db.contents.apply {
            val articleId = postArticle(
                article = article,
                authorId = userIdMap[author]!!,
                courseCode = courseCode,
            )!!
            questions.map { questionRequest -> questionRequest.submit(db, userIdMap, articleId) }
            comments.map { commentRequest -> commentRequest.submit(db, userIdMap, articleId) }
        }
    }
}

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

    @PublishedApi
    internal var comments: MutableList<CommentPostRequest> = mutableListOf()

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

    @SampleDataDslMarker
    inline fun comment(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author, isAnswer = true).apply(builds).build())
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
        questions = questions,
        comments = comments,
    )
}