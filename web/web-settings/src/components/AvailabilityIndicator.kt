package org.solvo.web.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@Composable
fun AvailabilityIndicator(
    property: AutoCheckProperty<*, *>
) {
    val hasError by property.hasError.collectAsState()
    AvailabilityIndicator(hasError)
}

@Composable
fun AvailabilityIndicator(isAvailable: Boolean?) {
    when (isAvailable) {
        null -> {
            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
        }

        true -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.primary.copy(
                        0.7f
                    )
                ) {
                    Icon(Icons.Outlined.Check, null, Modifier.clip(CircleShape))
//                    Text("Available", Modifier.padding(start = 6.dp, end = 12.dp))
                }
            }
        }

        else -> {}
    }
}
