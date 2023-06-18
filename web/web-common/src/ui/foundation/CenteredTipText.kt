package org.solvo.web.ui.foundation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CenteredTipText(
    text: @Composable RowScope.() -> Unit,
) {
    Box(Modifier.padding(vertical = 36.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
        ProvideTextStyle(MaterialTheme.typography.titleLarge) {
            Row {
                text()
            }
        }
    }
}

@Composable
fun CenteredTipText(
    text: String,
) = CenteredTipText { Text(text) }
