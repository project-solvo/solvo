package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.Article
import org.solvo.model.Course
import org.solvo.model.Question
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ArticleTable
import org.solvo.server.database.exposed.CourseTable
import org.solvo.server.database.exposed.TermTable
import java.util.*

interface ArticleDBFacade : CommentedObjectDBFacade<Article> {
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
}

class ArticleDBFacadeImpl : ArticleDBFacade {
    private val commentedObjectDB = CommentedObjectDBFacadeImpl<Article>()
    private val questionDB = QuestionDBFacadeImpl()

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
        val articleId = commentedObjectDB.post(content) ?: return@dbQuery null

        val courseId = getOrInsertCourseId(content.course)
        val termId = getOrInsertTermId(content.termYear)

        assert(ArticleTable.insert {
            it[ArticleTable.coid] = articleId
            it[ArticleTable.course] = courseId
            it[ArticleTable.term] = termId
        }.resultedValues != null)

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
            ?: TermTable
                .insert { it[TermTable.termTime] = termTime }
                .resultedValues
                ?.map { it[TermTable.id].value }
                ?.singleOrNull()!!
    }

    private suspend fun getOrInsertCourseId(course: Course): Int = dbQuery {
        CourseTable
            .select(CourseTable.courseName eq course.toString())
            .map { it[CourseTable.id].value }
            .singleOrNull()
            ?: CourseTable
                .insert { it[CourseTable.courseName] = course.toString() }
                .resultedValues
                ?.map { it[CourseTable.id].value }
                ?.singleOrNull()!!
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
