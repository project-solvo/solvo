package org.solvo.web.dummy

import org.solvo.model.annotations.Immutable
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentKind
import org.solvo.model.api.communication.User
import org.solvo.model.foundation.randomUuid
import org.solvo.model.utils.nonBlank

@Immutable
object Loading {
    val User = User(
        id = randomUuid(),
        username = "Loading...".nonBlank,
        avatarUrl = null,
    )

    val CommentDownstream = CommentDownstream(
        coid = randomUuid(),
        author = User,
        content = "",
        anonymity = false,
        likes = 0u,
        dislikes = 0u,
        parent = randomUuid(),
        pinned = false,
        postTime = 0,
        lastEditTime = 0,
        lastCommentTime = 0,
        previewSubComments = listOf(),
        allSubCommentIds = listOf(),
        kind = CommentKind.COMMENT,
    )
}