package org.solvo.server.database

import org.solvo.model.*
import org.solvo.server.ServerContext
import org.solvo.server.database.control.*
import org.solvo.server.utils.StaticResourcePurpose
import java.io.File
import java.io.InputStream
import java.util.*

interface ContentDBFacade {
    suspend fun newCourse(course: Course): Int?
    suspend fun postArticle(article: ArticleUpstream, authorId: UUID, courseCode: String): UUID?
    suspend fun postSharedContent(content: SharedContent): UUID?
    suspend fun postQuestion(question: QuestionUpstream, authorId: UUID, articleId: UUID, code: String): UUID?
    suspend fun postAnswer(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID?
    suspend fun postImage(uid: UUID, input: InputStream, purpose: StaticResourcePurpose): UUID
    suspend fun getImage(resourceId: UUID): File?
    suspend fun allCourses(): List<Course>
    suspend fun allArticlesOfCourse(courseCode: String): List<ArticleDownstream>?
    suspend fun getArticleId(courseCode: String, code: String): UUID?
    suspend fun viewArticle(articleId: UUID): ArticleDownstream?
    suspend fun getQuestionId(articleId: UUID, code: String): UUID?
    suspend fun viewQuestion(questionId: UUID): QuestionDownstream?
    suspend fun viewQuestion(articleId: UUID, index: String): QuestionDownstream?
    suspend fun tryDeleteImage(resourceId: UUID): Boolean
    suspend fun getCourseName(courseCode: String): String?
    suspend fun postComment(comment: CommentUpstream, authorId: UUID, parentId: UUID): UUID?
    suspend fun viewComment(commentId: UUID): CommentDownstream?
}

class ContentDBFacadeImpl(
    private val courses: CourseDBControl,
    private val articles: ArticleDBControl,
    private val questions: QuestionDBControl,
    private val comments: CommentDBControl,
    private val sharedContents: SharedContentDBControl,
    private val resources: ResourcesDBControl,
) : ContentDBFacade {
    override suspend fun newCourse(course: Course): Int? {
        if (courses.getId(course.code) != null) return null
        return courses.insert(course)
    }

    override suspend fun postArticle(article: ArticleUpstream, authorId: UUID, courseCode: String): UUID? {
        return articles.post(article, authorId, courseCode)
    }

    override suspend fun postSharedContent(content: SharedContent): UUID? {
        return sharedContents.post(content)
    }

    override suspend fun postQuestion(
        question: QuestionUpstream,
        authorId: UUID,
        articleId: UUID,
        code: String
    ): UUID? {
        if (!articles.contains(articleId)) return null
        return questions.post(question, authorId, articleId, code)
    }

    override suspend fun postAnswer(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID? {
        if (!questions.contains(questionId)) return null
        return comments.post(answer, authorId, questionId, asAnswer = true)
    }

    override suspend fun postComment(comment: CommentUpstream, authorId: UUID, parentId: UUID): UUID? {
        return comments.post(comment, authorId, parentId, asAnswer = false)
    }

    override suspend fun postImage(
        uid: UUID,
        input: InputStream,
        purpose: StaticResourcePurpose,
    ): UUID {
        val newImageId = resources.addResource(purpose)
        val path = ServerContext.paths.staticResourcePath(newImageId, purpose)

        ServerContext.files.write(input, path)
        return newImageId
    }

    override suspend fun getImage(resourceId: UUID): File? {
        val purpose = resources.getPurpose(resourceId) ?: return null

        val path = ServerContext.paths.staticResourcePath(resourceId, purpose)
        return File(path)
    }

    override suspend fun allCourses(): List<Course> {
        return courses.all()
    }

    override suspend fun getCourseName(courseCode: String): String? {
        val courseId = courses.getId(courseCode) ?: return null
        return courses.getCourse(courseId)?.name
    }

    override suspend fun allArticlesOfCourse(courseCode: String): List<ArticleDownstream>? {
        val courseId = courses.getId(courseCode) ?: return null
        return articles.viewAll(courseId)
    }

    override suspend fun getArticleId(courseCode: String, code: String): UUID? {
        return articles.getId(courseCode, code)
    }

    override suspend fun viewArticle(articleId: UUID): ArticleDownstream? {
        return articles.view(articleId)
    }

    override suspend fun getQuestionId(articleId: UUID, code: String): UUID? {
        return questions.getId(articleId, code)
    }

    override suspend fun viewQuestion(questionId: UUID): QuestionDownstream? {
        return questions.view(questionId)
    }

    override suspend fun viewComment(commentId: UUID): CommentDownstream? {
        return comments.view(commentId)
    }

    override suspend fun viewQuestion(articleId: UUID, index: String): QuestionDownstream? {
        val questionId = questions.getId(articleId, index) ?: return null
        return questions.view(questionId)
    }

    override suspend fun tryDeleteImage(resourceId: UUID): Boolean {
        return resources.tryDeleteResource(resourceId)
    }
}