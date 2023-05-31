package org.solvo.server.utils.sampleData


class CoursePostRequest(
    val code: String,
    val name: String,
    val articles: List<ArticlePostRequest>,
)

@SampleDataDslMarker
class CoursePostRequestBuilder(
    private var code: String,
    private var name: String
) {
    private val articles: MutableList<ArticlePostRequest> = mutableListOf()

    @SampleDataDslMarker
    fun article(
        code: String,
        author: UserRegisterRequest,
        builds: ArticlePostRequestBuilder.() -> Unit
    ): ArticlePostRequest {
        return ArticlePostRequestBuilder(code, author).apply(builds).build().also { articles.add(it) }
    }

    fun build(): CoursePostRequest {
        return CoursePostRequest(code, name, articles)
    }
}