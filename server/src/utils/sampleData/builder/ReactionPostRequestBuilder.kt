package org.solvo.server.utils.sampleData.builder

import org.solvo.model.api.communication.ReactionKind
import org.solvo.server.ServerContext
import org.solvo.server.utils.sampleData.SampleDataDslMarker
import java.util.*


class ReactionPostRequest(
    val user: UserRegisterRequest,
    val reactions: MutableList<ReactionKind>,
) {
    suspend fun submit(
        db: ServerContext.Databases,
        parentId: UUID,
    ) {
        reactions.forEach {reaction ->
            db.contents.postReaction(parentId, user.uid, reaction)
        }
    }
}

@SampleDataDslMarker
class ReactionPostRequestBuilder(
    private val user: UserRegisterRequest,
) {
    private val reactions: MutableList<ReactionKind> = mutableListOf()

    fun react(kind: ReactionKind) {
        reactions.add(kind)
    }

    fun build(): ReactionPostRequest {
        return ReactionPostRequest(user, reactions)
    }
}