package org.solvo.web.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.model.Comment


@Composable
fun CommentCard(
    comment: Comment,
) {
    val shape = RoundedCornerShape(8.dp)
    Card(Modifier.width(IntrinsicSize.Min), shape = shape) {


        Divider(Modifier.fillMaxWidth())

        Column(Modifier.background(Color(0x212121), shape = shape)) {

        }
    }
}