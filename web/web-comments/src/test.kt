package org.solvo.web.comments

import androidx.compose.ui.Modifier
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.subComments.CommentColumn
import org.solvo.web.dummy.createDummyText
import org.solvo.web.ui.SolvoWindow


fun main() {
    val context = createDummyText(123);

    val commentDownstream1 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, 0, 0, 0, listOf(), listOf()
    )
    val commentDownstream2 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, 0, 0, 0, listOf(), listOf()
    )
    onWasmReady {
        SolvoWindow {
            CommentColumn(
                listOf(
                    commentDownstream1, commentDownstream2,
                    commentDownstream1, commentDownstream2, commentDownstream2
                ),
                Modifier
            )
        }
    }
}

