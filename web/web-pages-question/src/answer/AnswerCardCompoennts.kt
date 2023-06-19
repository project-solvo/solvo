package org.solvo.web.answer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.solvo.web.DraftKind

@Composable
fun AnswerCardDate(
    postTimeFormatted: String,
    thoughtTip: @Composable () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(postTimeFormatted)
        thoughtTip()
    }
}

@Composable
fun ThoughtTip(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(DraftKind.Thought.icon, null, Modifier.height(20.dp))
        Text(
            "This might not be a complete answer",
            Modifier.padding(start = 8.dp),
            fontWeight = FontWeight.W400,
            overflow = TextOverflow.Ellipsis
        )
    }
}
