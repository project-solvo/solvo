package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.model.ArticleDownstream
import org.solvo.model.ArticleUpstream
import org.solvo.model.User
import org.solvo.model.utils.DatabaseModel
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ArticleTable
import java.util.*

interface ArticleDBFacade : CommentedObjectDBFacade<ArticleUpstream> {
    suspend fun getId(courseCode: String, term: String, name: String): UUID?
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
}

class ArticleDBFacadeImpl(
    private val courseDB: CourseDBFacade,
    private val termDB: TermDBFacade,
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

    override suspend fun view(coid: UUID): ArticleDownstream? {
        TODO("Not yet implemented")
    }

    override suspend fun star(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unStar(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
