package org.solvo.server.database

import org.solvo.model.Article
import java.util.*

interface ArticleDBFacade: CommentedObjectDBFacade<Article> {
    suspend fun star(uid: UUID, coid: UUID): Boolean
    suspend fun unStar(uid: UUID, coid: UUID): Boolean
}