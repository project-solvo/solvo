package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.ArticleEditRequest
import org.solvo.model.api.communication.ArticleUpstream
import org.solvo.model.utils.ModelConstraints
import org.solvo.model.utils.NonBlankString
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ArticleTable
import org.solvo.server.database.exposed.CommentTable
import org.solvo.server.database.exposed.CommentedObjectTable
import org.solvo.server.database.exposed.QuestionTable
import java.util.*

interface ArticleDBControl : CommentedObjectDBControl<ArticleUpstream> {
    suspend fun post(content: ArticleUpstream, authorId: UUID, courseCode: String): UUID?

    suspend fun create(articleCode: NonBlankString, authorId: UUID, courseCode: String): UUID?
    suspend fun edit(request: ArticleEditRequest, userId: UUID, articleId: UUID): Boolean
    suspend fun getId(courseCode: String, code: String): UUID?
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
    suspend fun viewAll(courseId: Int): List<ArticleDownstream>
    suspend fun view(coid: UUID): ArticleDownstream?
}

class ArticleDBControlImpl(
    private val courseDB: CourseDBControl,
    private val termDB: TermDBControl,
    private val accountDB: AccountDBControl,
    private val textDB: TextDBControl,
) : ArticleDBControl, CommentedObjectDBControlImpl<ArticleUpstream>(textDB) {
    override val associatedTable = ArticleTable

    override suspend fun getId(courseCode: String, code: String): UUID? = dbQuery {
        val courseId = courseDB.getId(courseCode) ?: return@dbQuery null

        ArticleTable
            .select((ArticleTable.course eq courseId) and (ArticleTable.code eq code))
            .filter { it[ArticleTable.visible] }
            .map { it[ArticleTable.coid].value }
            .singleOrNull()
    }

    override suspend fun post(content: ArticleUpstream, authorId: UUID, courseCode: String): UUID? {
        if (content.termYear.str.length > ModelConstraints.TERM_TIME_MAX_LENGTH
            || content.code.str.length > ModelConstraints.ARTICLE_NAME_MAX_LENGTH
        ) return null
        val coid = insertAndGetCOID(content, authorId) ?: return null
        val courseId = courseDB.getId(courseCode) ?: return null
        val termId = termDB.getOrInsertId(content.termYear.str)

        dbQuery {
            assert(ArticleTable.insert {
                it[ArticleTable.coid] = coid
                it[ArticleTable.code] = content.code.str
                it[ArticleTable.displayName] = content.displayName.str
                it[ArticleTable.course] = courseId
                it[ArticleTable.term] = termId
            }.resultedValues?.singleOrNull() != null)
        }
        return coid
    }

    override suspend fun create(articleCode: NonBlankString, authorId: UUID, courseCode: String): UUID? {
        return post(ArticleUpstream(code = articleCode), authorId, courseCode)
    }

    override suspend fun edit(request: ArticleEditRequest, userId: UUID, articleId: UUID): Boolean = dbQuery {
        if (!contains(articleId)) return@dbQuery false
        if (request.termYear?.let { it.str.length > ModelConstraints.TERM_TIME_MAX_LENGTH } == true
            || request.code?.let { it.str.length > ModelConstraints.ARTICLE_NAME_MAX_LENGTH } == true
        ) return@dbQuery false

        request.run {
            anonymity?.let { anonymity -> setAnonymity(articleId, anonymity) }
            content?.let { content -> modifyContent(articleId, content.str) }
            ArticleTable
                .update({ ArticleTable.coid eq articleId }) {
                    request.code?.let { code -> it[ArticleTable.code] = code.str }
                    request.displayName?.let { displayName -> it[ArticleTable.displayName] = displayName.str }
                } > 0
        }
    }

    override suspend fun view(coid: UUID): ArticleDownstream? = dbQuery {
        val curViews = ArticleTable
            .select { ArticleTable.coid eq coid }
            .filter { it[ArticleTable.visible] }
            .map { it[ArticleTable.views] }
            .singleOrNull()
            ?: return@dbQuery null
        ArticleTable.update({ ArticleTable.coid eq coid }) { it[views] = curViews + 1u }

        val questionIndexes: List<String> = ArticleTable
            .join(QuestionTable, JoinType.INNER, ArticleTable.coid, QuestionTable.article)
            .select(ArticleTable.coid eq coid)
            .orderBy(QuestionTable.code, SortOrder.ASC)
            .filter { it[QuestionTable.visible] }
            .map { it[QuestionTable.code] }

        val comments: List<UUID> = ArticleTable
            .join(CommentTable, JoinType.INNER, ArticleTable.coid, CommentTable.parent)
            .select(ArticleTable.coid eq coid)
            .filter { it[CommentTable.visible] }
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
                    content = it[CommentedObjectTable.content].value.let { textId ->
                        textDB.view(textId) ?: error("text not found with id $textId")
                    },
                    anonymity = it[CommentedObjectTable.anonymity],
                    likes = it[CommentedObjectTable.likes],
                    dislikes = it[CommentedObjectTable.dislikes],
                    code = it[ArticleTable.code],
                    displayName = it[ArticleTable.displayName],
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
            .filter { it[ArticleTable.visible] }
            .mapNotNull { view(it[ArticleTable.coid].value) }
    }

    override suspend fun star(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unStar(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
