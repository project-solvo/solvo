package org.solvo.web.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

private const val LOADABLE_CONTENT_ANIMATION = true

@Composable
fun LoadableContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier) {
        if (LOADABLE_CONTENT_ANIMATION) {
            AnimatedVisibility(
                !isLoading,
                enter = slideInVertically { 0 } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
            ) {
                content()
            }

            AnimatedVisibility(
                isLoading,
                enter = slideInVertically { 0 } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
            ) {
                Row(Modifier.fillMaxWidth().padding(start = 64.dp), horizontalArrangement = Arrangement.Center) {
                    loadingContent()
                }
            }
        } else {
            if (isLoading) {
                Row(Modifier.fillMaxWidth().padding(start = 64.dp), horizontalArrangement = Arrangement.Center) {
                    loadingContent()
                }
            } else {
                content()
            }
        }
    }
}

@Composable
fun OverlayLoadableContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.wrapContentSize()) {
        Box(Modifier.alpha(if (isLoading) 0.618f else 1.0f)) {
            content()
        }

        AnimatedVisibility(
            isLoading,
            enter = enter,
            exit = exit,
        ) {
            Row(Modifier.wrapContentHeight().fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                loadingContent()
            }
        }
    }
}