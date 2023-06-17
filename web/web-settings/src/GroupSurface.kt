package org.solvo.web.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


/**
 * A titled section card
 */
@Composable
fun Section(
    header: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(modifier.padding(vertical = 16.dp).clip(shape), shape = shape, tonalElevation = 1.dp) {
        Column {
            Surface(Modifier.height(64.dp).fillMaxWidth(), tonalElevation = 8.dp) {
                ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                    Row(
                        Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        header()
                    }
                }
            }

            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    content()
                }
            }
        }
    }
}
