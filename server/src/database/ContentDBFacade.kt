package org.solvo.server.database

import org.solvo.model.api.LiteralChecker
import org.solvo.model.api.communication.*
import org.solvo.model.utils.NonBlankString
import org.solvo.server.database.control.*
import java.util.*

interface ContentDBFacade {
    suspend fun newCourse(course: Course): Int?
    suspend fun allCourses(): List<Course>
    suspend fun allArticlesOfCourse(courseCode: String): List<ArticleDownstream>?
    suspend fun getCourseName(courseCode: String): String?
    suspend fun getCourseId(courseCode: String): Int?
    suspend fun editCourse(courseId: Int, course: Course): Boolean

    suspend fun createArticle(articleCode: NonBlankString, authorId: UUID, courseCode: String): UUID?
    suspend fun editArticle(request: ArticleEditRequest, userId: UUID, articleId: UUID): Boolean
    suspend fun getArticleId(courseCode: String, code: String): UUID?
    suspend fun viewArticle(articleId: UUID): ArticleDownstream?
    suspend fun deleteArticle(articleId: UUID): Boolean

    suspend fun postSharedContent(content: SharedContent): UUID?
    suspend fun createQuestion(questionCode: NonBlankString, articleId: UUID, authorId: UUID): UUID?
    suspend fun editQuestion(request: QuestionEditRequest, userId: UUID, questionId: UUID): Boolean
    suspend fun getQuestionId(articleId: UUID, code: String): UUID?
    suspend fun viewQuestion(questionId: UUID): QuestionDownstream?
    suspend fun viewQuestion(articleId: UUID, index: String): QuestionDownstream?
    suspend fun deleteQuestion(questionId: UUID): Boolean

    suspend fun postAnswer(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID?
    suspend fun postComment(comment: CommentUpstream, authorId: UUID, parentId: UUID): UUID?
    suspend fun postThought(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID?
    suspend fun editComment(request: CommentEditRequest, commentId: UUID, userId: UUID): Boolean
    suspend fun viewComment(commentId: UUID, uid: UUID? = null): CommentDownstream?
    suspend fun getCommentParentId(coid: UUID): UUID?
    suspend fun deleteComment(commentId: UUID, userId: UUID): Boolean

    suspend fun viewAllReactions(targetId: UUID, userId: UUID?): List<Reaction>
    suspend fun viewUsersOfReaction(targetId: UUID, kind: ReactionKind): List<UUID>
    suspend fun postReaction(targetId: UUID, userId: UUID, reaction: ReactionKind): Boolean
    suspend fun deleteReaction(targetId: UUID, userId: UUID, reaction: ReactionKind): Boolean
}

class ContentDBFacadeImpl(
    private val courses: CourseDBControl,
    private val articles: ArticleDBControl,
    private val questions: QuestionDBControl,
    private val comments: CommentDBControl,
    private val texts: TextDBControl,
    private val reactions: ReactionDBControl,
) : ContentDBFacade {
    override suspend fun newCourse(course: Course): Int? {
        if (!LiteralChecker.checkCourseCode(course.code.str)) return null
        if (courses.getId(course.code.str) != null) return null
        return courses.insert(course)
    }

    override suspend fun createArticle(articleCode: NonBlankString, authorId: UUID, courseCode: String): UUID? {
        if (!LiteralChecker.checkArticleCode(articleCode.str)) return null
        return articles.create(articleCode, authorId, courseCode)
    }

    override suspend fun editArticle(request: ArticleEditRequest, userId: UUID, articleId: UUID): Boolean {
        request.code?.let { if (!LiteralChecker.checkArticleCode(it.str)) return false }
        return articles.edit(request, userId, articleId)
    }

    override suspend fun postSharedContent(content: SharedContent): UUID? {
        return texts.post(content.content)
    }

    override suspend fun createQuestion(questionCode: NonBlankString, articleId: UUID, authorId: UUID): UUID? {
        if (!LiteralChecker.checkQuestionCode(questionCode.str)) return null
        if (!articles.contains(articleId)) return null
        return questions.create(authorId, articleId, questionCode)
    }

    override suspend fun editQuestion(request: QuestionEditRequest, userId: UUID, questionId: UUID): Boolean {
        request.code?.let { if (!LiteralChecker.checkQuestionCode(it.str)) return false }
        return questions.edit(request, userId, questionId)
    }

    override suspend fun postAnswer(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID? {
        if (!questions.contains(questionId)) return null
        return comments.post(answer, authorId, questionId, CommentKind.ANSWER)
    }

    override suspend fun postThought(answer: CommentUpstream, authorId: UUID, questionId: UUID): UUID? {
        if (!questions.contains(questionId)) return null
        return comments.post(answer, authorId, questionId, CommentKind.THOUGHT)
    }

    override suspend fun postComment(comment: CommentUpstream, authorId: UUID, parentId: UUID): UUID? {
        return comments.post(comment, authorId, parentId, CommentKind.COMMENT)
    }

    override suspend fun editComment(request: CommentEditRequest, commentId: UUID, userId: UUID): Boolean {
        return comments.getAuthorId(commentId) == userId && comments.edit(request, commentId)
    }

    override suspend fun deleteComment(commentId: UUID, userId: UUID): Boolean {
        return comments.getAuthorId(commentId) == userId && comments.delete(commentId)
    }

    override suspend fun allCourses(): List<Course> {
        return courses.all()
    }

    override suspend fun getCourseName(courseCode: String): String? {
        val courseId = courses.getId(courseCode) ?: return null
        return courses.getCourse(courseId)?.name?.toString()
    }

    override suspend fun getCourseId(courseCode: String): Int? {
        return courses.getId(courseCode)
    }

    override suspend fun editCourse(courseId: Int, course: Course): Boolean {
        return courses.edit(courseId, course)
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

    override suspend fun deleteArticle(articleId: UUID): Boolean {
        return articles.delete(articleId)
    }

    override suspend fun getQuestionId(articleId: UUID, code: String): UUID? {
        return questions.getId(articleId, code)
    }

    override suspend fun viewQuestion(questionId: UUID): QuestionDownstream? {
        return questions.view(questionId)
    }

    override suspend fun viewComment(commentId: UUID, uid: UUID?): CommentDownstream? {
        return comments.view(commentId, uid)
    }

    override suspend fun getCommentParentId(coid: UUID): UUID? {
        return comments.getParentId(coid)
    }

    override suspend fun viewQuestion(articleId: UUID, index: String): QuestionDownstream? {
        val questionId = questions.getId(articleId, index) ?: return null
        return questions.view(questionId)
    }

    override suspend fun deleteQuestion(questionId: UUID): Boolean {
        return questions.delete(questionId)
    }

    override suspend fun postReaction(targetId: UUID, userId: UUID, reaction: ReactionKind): Boolean {
        return reactions.post(userId, targetId, reaction)
    }

    override suspend fun deleteReaction(targetId: UUID, userId: UUID, reaction: ReactionKind): Boolean {
        return reactions.delete(userId, targetId, reaction)
    }

    override suspend fun viewAllReactions(targetId: UUID, userId: UUID?): List<Reaction> {
        return reactions.getAllReactions(userId, targetId)
    }

    override suspend fun viewUsersOfReaction(targetId: UUID, kind: ReactionKind): List<UUID> {
        return reactions.getUserIds(targetId, kind)
    }
}