package org.solvo.server.database

import org.jetbrains.exposed.sql.Table
import org.solvo.model.CommentUpstream
import org.solvo.model.CommentableDownstream
import org.solvo.model.User
import org.solvo.server.database.exposed.CommentTable
import java.util.*

interface CommentDBFacade: CommentedObjectDBFacade<CommentUpstream> {
    suspend fun pin(uid: UUID, coid: UUID): Boolean
    suspend fun unpin(uid: UUID, coid: UUID): Boolean
}

class CommentDBFacadeImpl : CommentDBFacade, CommentedObjectDBFacadeImpl<CommentUpstream>() {
    override val associatedTable: Table = CommentTable
    override suspend fun pin(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unpin(uid: UUID, coid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun associateTableUpdates(coid: UUID, content: CommentUpstream, author: User): UUID? {
        TODO("Not yet implemented")
    }

    override suspend fun view(coid: UUID): CommentableDownstream? {
        TODO("Not yet implemented")
    }

}
