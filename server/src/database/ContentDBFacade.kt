package org.solvo.server.database

import org.solvo.model.api.communication.*
import org.solvo.server.database.control.*
import java.util.*

interface ContentDBFacade {
    suspend fun newCourse(course: Course): Int?
    suspend fun postArticle(article: ArticleUpstream, authorId: UUID, courseCode: String): UUID?
    suspend fun postSharedContent(content: SharedContent): UUID?
    suspend fun postQuestion(question: QuestionUpstream, authorId: UUID, articleId: UUID, code: String): UUID?
    suspend fun postAnswer(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID?
    suspend fun allCourses(): List<Course>
    suspend fun allArticlesOfCourse(courseCode: String): List<ArticleDownstream>?
    suspend fun getArticleId(courseCode: String, code: String): UUID?
    suspend fun viewArticle(articleId: UUID): ArticleDownstream?
    suspend fun getQuestionId(articleId: UUID, code: String): UUID?
    suspend fun viewQuestion(questionId: UUID): QuestionDownstream?
    suspend fun viewQuestion(articleId: UUID, index: String): QuestionDownstream?
    suspend fun getCourseName(courseCode: String): String?
    suspend fun postComment(comment: CommentUpstream, authorId: UUID, parentId: UUID): UUID?
    suspend fun viewComment(commentId: UUID): CommentDownstream?
    suspend fun viewAllReactions(targetId: UUID, userId: UUID?): List<Reaction>
    suspend fun postReaction(targetId: UUID, userId: UUID, reaction: ReactionKind): Boolean
}

class ContentDBFacadeImpl(
    private val courses: CourseDBControl,
    private val articles: ArticleDBControl,
    private val questions: QuestionDBControl,
    private val comments: CommentDBControl,
    private val sharedContents: SharedContentDBControl,
    private val reactions: ReactionDBControl,
) : ContentDBFacade {
    override suspend fun newCourse(course: Course): Int? {
        if (courses.getId(course.code.toString()) != null) return null
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

    override suspend fun allCourses(): List<Course> {
        return courses.all()
    }

    override suspend fun getCourseName(courseCode: String): String? {
        val courseId = courses.getId(courseCode) ?: return null
        return courses.getCourse(courseId)?.name?.toString()
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

    override suspend fun postReaction(targetId: UUID, userId: UUID, reaction: ReactionKind): Boolean {
        return reactions.post(userId, targetId, reaction)
    }

    override suspend fun viewAllReactions(targetId: UUID, userId: UUID?): List<Reaction> {
        return reactions.getAllReactions(userId, targetId)
    }
}