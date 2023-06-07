package org.solvo.server.database.exposed

import org.jetbrains.exposed.sql.Table
import org.solvo.model.ReactionKind

object ReactionTable: Table() {
    val target = reference("target", CommentedObjectTable)
    val user = reference("user", UserTable)
    val reaction = enumeration<ReactionKind>("reaction")

    override val primaryKey = PrimaryKey(target, user, reaction)
}