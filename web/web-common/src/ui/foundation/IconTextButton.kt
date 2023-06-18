package org.solvo.web.ui.foundation

import androidx.compose.foundation.Indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon


@Composable
fun IconTextButton(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indication: Indication? = null,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
        Box(
            modifier.clip(RoundedCornerShape(12.dp))
                .clickable(indication = indication, onClick = onClick)
                .cursorHoverIcon(CursorIcon.POINTER)
        ) {
            Row(
                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.padding(vertical = 2.dp)) {
                    icon()
                }

                ProvideTextStyle(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                    )
                ) {
                    text()
                }
            }
        }
    }
}