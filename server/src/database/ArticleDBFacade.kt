package org.solvo.server.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.ArticleDownstream
import org.solvo.model.ArticleUpstream
import org.solvo.model.User
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ArticleTable
import org.solvo.server.database.exposed.CommentedObjectTable
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface ArticleDBFacade : CommentedObjectDBFacade<ArticleUpstream> {
    suspend fun getId(courseCode: String, term: String, name: String): UUID?
    suspend fun getExistingTermsOfCourse(courseId: Int): List<String>
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
    suspend fun viewAll(courseId: Int, termId: Int): List<ArticleDownstream>
    override suspend fun view(coid: UUID): ArticleDownstream?
}

class ArticleDBFacadeImpl(
    private val courseDB: CourseDBFacade,
    private val termDB: TermDBFacade,
    private val accountDB: AccountDBFacade,
) : ArticleDBFacade, CommentedObjectDBFacadeImpl<ArticleUpstream>() {
    override val associatedTable: Table = ArticleTable

    override suspend fun getId(courseCode: String, term: String, name: String): UUID? = dbQuery {
        val courseId = courseDB.getId(courseCode) ?: return@dbQuery null
        val termId = termDB.getId(term) ?: return@dbQuery null

        ArticleTable
            .select(
                (ArticleTable.course eq courseId)
                        and (ArticleTable.term eq termId)
                        and (ArticleTable.name eq name)
            ).map { it[ArticleTable.coid].value }
            .singleOrNull()
    }

    override suspend fun getExistingTermsOfCourse(courseId: Int): List<String> = dbQuery {
        ArticleTable
            .select(ArticleTable.course eq courseId)
            .map { termDB.getTerm(it[ArticleTable.term].value)!! }
            .distinct()
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !ArticleTable.select(ArticleTable.coid eq coid).empty()
    }

    override suspend fun post(content: ArticleUpstream, author: User): UUID? = dbQuery {
        if (content.course.code.length > DatabaseModel.COURSE_CODE_MAX_LENGTH
            || content.course.name.length > DatabaseModel.COURSE_NAME_MAX_LENGTH
            || content.termYear.length > DatabaseModel.TERM_TIME_MAX_LENGTH
            || content.name.length > DatabaseModel.ARTICLE_NAME_MAX_LENGTH
        ) {
            return@dbQuery null
        }
        super.post(content, author)
    }

    override suspend fun associateTableUpdates(coid: UUID, content: ArticleUpstream, author: User): UUID = dbQuery {
        val courseId = courseDB.getOrInsertId(content.course)
        val termId = termDB.getOrInsertId(content.termYear)

        assert(ArticleTable.insert {
            it[ArticleTable.coid] = coid
            it[ArticleTable.name] = content.name
            it[ArticleTable.course] = courseId
            it[ArticleTable.term] = termId
        }.resultedValues?.singleOrNull() != null)

        return@dbQuery coid
    }

    override suspend fun view(coid: UUID): ArticleDownstream? = dbQuery {
        val curViews = ArticleTable.select { ArticleTable.coid eq coid }.map { it[ArticleTable.views] }.singleOrNull()
            ?: return@dbQuery null
        ArticleTable.update ({ ArticleTable.coid eq coid }) { it[views] = curViews + 1u }

        val questionIndexes: List<String> = ArticleTable
            .join(QuestionTable, JoinType.INNER, ArticleTable.coid, QuestionTable.article)
            .select(ArticleTable.coid eq coid)
            .map { it[QuestionTable.index] }
            .sorted()

        ArticleTable
            .join(CommentedObjectTable, JoinType.INNER, ArticleTable.coid, CommentedObjectTable.id)
            .select(ArticleTable.coid eq coid)
            .map {
                ArticleDownstream(
                    coid = it[ArticleTable.coid].value,
                    author = if (it[CommentedObjectTable.anonymity]) {
                        null
                    } else {
                        accountDB.getUserInfo(it[CommentedObjectTable.author].value)!!
                    },
                    content = it[CommentedObjectTable.content],
                    anonymity = it[CommentedObjectTable.anonymity],
                    likes = it[CommentedObjectTable.likes],
                    name = it[ArticleTable.name],
                    course = courseDB.getCourse(it[ArticleTable.course].value)!!,
                    termYear = termDB.getTerm(it[ArticleTable.term].value)!!,
                    questionIndexes = questionIndexes,
                    comments = listOf(), // TODO
                    stars = it[ArticleTable.stars],
                    views = it[ArticleTable.views],
                )
            }.singleOrNull()
    }

    override suspend fun viewAll(courseId: Int, termId: Int): List<ArticleDownstream> = dbQuery {
        ArticleTable
            .select { (ArticleTable.course eq courseId) and (ArticleTable.term eq termId) }
            .mapNotNull { view(it[ArticleTable.coid].value) }
    }

    override suspend fun star(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unStar(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
