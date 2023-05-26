package org.solvo.web.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadableContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            isLoading,
            enter = slideInVertically { 0 } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            Row(Modifier.fillMaxWidth().padding(start = 64.dp), horizontalArrangement = Arrangement.Center) {
                loadingContent()
            }
        }

        AnimatedVisibility(
            !isLoading,
            enter = slideInVertically { 0 } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            // Course title
            content()
        }
    }
}