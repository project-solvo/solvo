package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.solvo.model.Article
import org.solvo.model.Course
import org.solvo.model.Question
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ArticleTable
import org.solvo.server.database.exposed.CourseTable
import org.solvo.server.database.exposed.TermTable
import java.util.*

interface ArticleDBFacade : CommentedObjectDBFacade<Article> {
    suspend fun getId(courseCode: String, term: String, name: String): UUID?
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
}

class ArticleDBFacadeImpl : ArticleDBFacade {
    private val commentedObjectDB = CommentedObjectDBFacadeImpl<Article>()
    private val questionDB = QuestionDBFacadeImpl()

    override suspend fun getId(courseCode: String, term: String, name: String): UUID? = dbQuery {
        val courseId = CourseTable
            .select(CourseTable.code eq courseCode)
            .map { it[CourseTable.id] }
            .singleOrNull()
            ?: return@dbQuery null
        val termId = TermTable
            .select(TermTable.termTime eq term)
            .map { it[TermTable.id] }
            .singleOrNull()
            ?: return@dbQuery null
        ArticleTable
            .select(
                (ArticleTable.course eq courseId)
                        and (ArticleTable.term eq termId)
                        and (ArticleTable.name eq name)
            ).map { it[ArticleTable.coid].value }
            .singleOrNull()
    }

    override suspend fun star(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unStar(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !ArticleTable.select(ArticleTable.coid eq coid).empty()
    }

    override suspend fun post(content: Article): UUID? = dbQuery {
        if (content.course.code.length > DatabaseModel.COURSE_CODE_MAX_LENGTH
            || content.course.name.length > DatabaseModel.COURSE_NAME_MAX_LENGTH
            || content.termYear.length > DatabaseModel.TERM_TIME_MAX_LENGTH
            || content.name.length > DatabaseModel.ARTICLE_NAME_MAX_LENGTH
        ) {
            return@dbQuery null
        }

        val articleId = commentedObjectDB.post(content) ?: return@dbQuery null

        val courseId = getOrInsertCourseId(content.course)
        val termId = getOrInsertTermId(content.termYear)

        assert(ArticleTable.insert {
            it[ArticleTable.coid] = articleId
            it[ArticleTable.name] = content.name
            it[ArticleTable.course] = courseId
            it[ArticleTable.term] = termId
        }.resultedValues?.singleOrNull() != null)

        for (question: Question in content.questions) {
            assert(questionDB.post(question, articleId) != null)
        }

        articleId
    }

    private suspend fun getOrInsertTermId(termTime: String): Int = dbQuery {
        TermTable
            .select(TermTable.termTime eq termTime)
            .map { it[TermTable.id].value }
            .singleOrNull()
            ?: TermTable.insertAndGetId { it[TermTable.termTime] = termTime }.value
    }

    private suspend fun getOrInsertCourseId(course: Course): Int = dbQuery {
        CourseTable
            .select(CourseTable.code eq course.code)
            .map { it[CourseTable.id].value }
            .singleOrNull()
            ?: CourseTable.insertAndGetId {
                it[CourseTable.code] = course.code
                it[CourseTable.name] = course.name
            }.value
    }

    override suspend fun modify(content: Article): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun view(coid: UUID): Article? {
        TODO("Not yet implemented")
    }

    override suspend fun like(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unLike(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

}
