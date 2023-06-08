package org.solvo.web

import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.model.api.communication.User
import org.solvo.model.foundation.Uuid
import org.solvo.model.utils.NonBlankString
import org.solvo.web.dummy.createDummyText
import kotlin.random.Random
import kotlin.random.nextUInt


private var testCommentId = 0
fun createCommentDownstream(): CommentDownstream {
    val id = testCommentId++
    return CommentDownstream(
        Uuid.random(),
        if (Random.nextBoolean()) {
            null
        } else {
            User(Uuid.random(), NonBlankString.fromString("User $id"), null)
        },
        createDummyText(id),
        Random.nextBoolean(),
        Random.nextUInt(),
        Random.nextUInt(),
        Uuid.random(),
        false,
        0,
        0,
        0,
        listOf(
            LightCommentDownstream(
                User(id = Uuid.random(), NonBlankString.fromString("查尔斯"), null),
                "你是好人！"
            ),
            LightCommentDownstream(
                User(id = Uuid.random(), NonBlankString.fromString("Commenter2"), null),
                "[Image] Content 2"
            ),
        ),
        listOf(),
    )
}
