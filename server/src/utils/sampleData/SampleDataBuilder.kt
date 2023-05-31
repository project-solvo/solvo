package org.solvo.server.utils.sampleData

import org.solvo.server.ServerContext
import java.util.*

class UserRegisterRequest(
    val username: String,
    val password: ByteArray,
) {
    suspend fun submit(db: ServerContext.Databases, userIdMap: MutableMap<UserRegisterRequest, UUID>) {
        db.accounts.apply {
            register(username, password)
            val token = login(username, password).token
            userIdMap[this@UserRegisterRequest] = ServerContext.tokens.matchToken(token)!!
        }
    }
}

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

    suspend fun submit(db: ServerContext.Databases) {
        val userIdMap: MutableMap<UserRegisterRequest, UUID> = mutableMapOf()
        users.map { userRequest -> userRequest.submit(db, userIdMap) }
        courses.map { courseRequest -> courseRequest.submit(db, userIdMap) }
    }
}

suspend inline fun ServerContext.Databases.incorporateSampleData(builds: SampleDataBuilder.() -> Unit) {
    SampleDataBuilder().apply { builds() }.submit(this)
}
