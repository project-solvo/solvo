package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.UUIDTable
import org.solvo.server.ServerContext

object CommentedObjectTable: UUIDTable("CommentedObjects", "COID") {
    val author = reference("userId", UserTable)
    val content = largeText("content")

    val anonymity = bool("anonymity").default(false)
    val likes = uinteger("likesCount").default(0u)
    val dislikes = uinteger("dislikesCount").default(0u)
    val comments = uinteger("commentsCount").default(0u)

    val postTime = long("postTime").default(ServerContext.localtime.now())
    val lastEditTime = long("lastEditTime").default(ServerContext.localtime.now())
    val lastCommentTime = long("lastCommentTime").default(ServerContext.localtime.now())
}