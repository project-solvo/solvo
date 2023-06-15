package org.solvo.web.ui.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextWithLeadingIcon(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        icon()
        Box(Modifier.padding(start = 6.dp)) {
            text()
        }
    }
}