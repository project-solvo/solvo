package org.solvo.server.utils.sampleData

import org.solvo.model.Course
import org.solvo.server.ServerContext
import java.util.*

class UserRegisterRequest(
    val username: String,
    val password: ByteArray,
)

@SampleDataDslMarker
class SampleDataBuilder {
    private val users: MutableList<UserRegisterRequest> = mutableListOf()
    @PublishedApi
    internal val courses: MutableList<CoursePostRequest> = mutableListOf()

    @SampleDataDslMarker
    fun user(username: String, password: ByteArray): UserRegisterRequest {
        return UserRegisterRequest(username, password).also { users.add(it) }
    }

    @SampleDataDslMarker
    inline fun course(
        code: String,
        name: String,
        builds: CoursePostRequestBuilder.() -> Unit = {},
    ): CoursePostRequest {
        return CoursePostRequestBuilder(code, name).apply(builds).build().also { courses.add(it) }
    }

    suspend fun build(db: ServerContext.Databases) {
        val userIdMap: MutableMap<UserRegisterRequest, UUID> = mutableMapOf()
        users.map { userRequest ->
            db.accounts.apply {
                register(userRequest.username, userRequest.password)
                val token = login(userRequest.username, userRequest.password).token
                userIdMap[userRequest] = ServerContext.tokens.matchToken(token)!!
            }
        }
        courses.map { course ->
            db.contents.apply {
                newCourse(Course(course.code, course.name))
                course.articles.map { articleRequest ->
                    postArticle(
                        article = articleRequest.article,
                        authorId = userIdMap[articleRequest.author]!!,
                        courseCode = course.code,
                    )
                }
            }
        }
    }
}

suspend inline fun ServerContext.Databases.incorporateSampleData(builds: SampleDataBuilder.() -> Unit) {
    SampleDataBuilder().apply { builds() }.build(this)
}
