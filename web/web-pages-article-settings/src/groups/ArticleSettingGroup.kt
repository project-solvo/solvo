package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.settings.SettingGroup

sealed class ArticleSettingGroup(
    pathName: String,
) : SettingGroup<PageViewModel>(pathName) {
    companion object {
        val articleSettingGroups = listOf(
            ArticlePropertiesSettingGroup
        )

    }
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
                    Icon(Icons.Outlined.Check, null)
                    Text("Available", Modifier.padding(start = 6.dp, end = 12.dp))
                }
            }
        }

        else -> {}
    }
}
