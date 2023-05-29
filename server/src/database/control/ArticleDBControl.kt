package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.ArticleDownstream
import org.solvo.model.ArticleUpstream
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ArticleTable
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface ArticleDBControl : CommentedObjectDBControl<ArticleUpstream> {
    suspend fun post(content: ArticleUpstream, authorId: UUID, courseCode: String): UUID?
    suspend fun getId(courseCode: String, name: String): UUID?
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
    suspend fun viewAll(courseId: Int): List<ArticleDownstream>
    override suspend fun view(coid: UUID): ArticleDownstream?
}

class ArticleDBControlImpl(
    private val courseDB: CourseDBControl,
    private val termDB: TermDBControl,
    private val accountDB: AccountDBControl,
) : ArticleDBControl, CommentedObjectDBControlImpl<ArticleUpstream>() {
    override val associatedTable: Table = ArticleTable

    override suspend fun getId(courseCode: String, name: String): UUID? = dbQuery {
        val courseId = courseDB.getId(courseCode) ?: return@dbQuery null

        ArticleTable
            .select(
                (ArticleTable.course eq courseId)
                        and (ArticleTable.name eq name)
            ).map { it[ArticleTable.coid].value }
            .singleOrNull()
    }

    override suspend fun contains(coid: UUID): Boolean = dbQuery {
        !ArticleTable.select(ArticleTable.coid eq coid).empty()
    }

    override suspend fun post(content: ArticleUpstream, authorId: UUID, courseCode: String): UUID? {
        if (content.termYear.length > DatabaseModel.TERM_TIME_MAX_LENGTH
            || content.name.length > DatabaseModel.ARTICLE_NAME_MAX_LENGTH
        ) return null
        val coid = insertAndGetCOID(content, authorId) ?: return null
        val courseId = courseDB.getId(courseCode) ?: return null
        val termId = termDB.getOrInsertId(content.termYear)

        dbQuery {
            assert(ArticleTable.insert {
                it[ArticleTable.coid] = coid
                it[ArticleTable.name] = content.name
                it[ArticleTable.course] = courseId
                it[ArticleTable.term] = termId
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun view(coid: UUID): ArticleDownstream? = dbQuery {
        val curViews = ArticleTable.select { ArticleTable.coid eq coid }.map { it[ArticleTable.views] }.singleOrNull()
            ?: return@dbQuery null
        ArticleTable.update({ ArticleTable.coid eq coid }) { it[views] = curViews + 1u }

        val questionIndexes: List<String> = ArticleTable
            .join(QuestionTable, JoinType.INNER, ArticleTable.coid, QuestionTable.article)
            .select(ArticleTable.coid eq coid)
            .orderBy(QuestionTable.index, SortOrder.ASC)
            .map { it[QuestionTable.index] }

        val comments: List<UUID> = ArticleTable
            .join(CommentTable, JoinType.INNER, ArticleTable.coid, CommentTable.parent)
            .select(ArticleTable.coid eq coid)
            .map { it[CommentTable.coid].value }

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
                    dislikes = it[CommentedObjectTable.dislikes],
                    name = it[ArticleTable.name],
                    course = courseDB.getCourse(it[ArticleTable.course].value)!!,
                    termYear = termDB.getTerm(it[ArticleTable.term].value)!!,
                    questionIndexes = questionIndexes,
                    comments = comments,
                    stars = it[ArticleTable.stars],
                    views = it[ArticleTable.views],
                )
            }.singleOrNull()
    }

    override suspend fun viewAll(courseId: Int): List<ArticleDownstream> = dbQuery {
        ArticleTable
            .select { ArticleTable.course eq courseId }
            .mapNotNull { view(it[ArticleTable.coid].value) }
    }

    override suspend fun star(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unStar(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
