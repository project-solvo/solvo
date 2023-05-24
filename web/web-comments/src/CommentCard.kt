package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Comment
import org.solvo.model.LightComment
import org.solvo.model.User
import org.solvo.model.foundation.Uuid
import org.solvo.web.document.SolvoWindow


@Composable
fun CommentCard(
    comment: Comment,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Card(modifier, shape = shape) {
        Column(Modifier.padding(horizontal = 8.dp).padding(top = 4.dp)) {
            Column {
                Text(comment.content)
            }

            Divider(Modifier.fillMaxWidth())

            val showComments by derivedStateOf { comment.subComments.take(3) }
            if (showComments.isNotEmpty()) {
                Column(Modifier.background(Color(0x212121), shape = shape)) {
                    for (subComment in showComments) {
                        CommentLine(subComment)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(32.dp)) {
                            Image(Icons.Default.Menu, "Comment Author", Modifier.size(32.dp))
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun CommentLine(subComment: LightComment) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(32.dp)) {
            Image(Icons.Default.Person, "Comment Author", Modifier.size(32.dp))
        }

        Text(subComment.authorName, Modifier.padding(start = 8.dp))

        Text(": ")
        Text(subComment.content)
    }
}

fun main() {
    onWasmReady {
        SolvoWindow {
            CommentCard(
                Comment(
                    Uuid.random(),
                    User(Uuid.random(), "Author", ""),
                    """
                        Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                    """.trimIndent(),
                    parent = Uuid.random(),
                    subComments = listOf(
                        LightComment(Uuid.random(), "Commenter1", "", "Content 1"),
                        LightComment(Uuid.random(), "Commenter2", "", "[Image] Content 2"),
                    ),
                )
            )
        }
    }
}