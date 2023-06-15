package org.solvo.web.ui.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun rememberBackgroundScope(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope {
    val ret = remember(coroutineContext) {
        val scope = CoroutineScope(coroutineContext)
        BackgroundScope(scope)
    }
    return ret.scope
}

private class BackgroundScope(
    @Stable
    val scope: CoroutineScope
) : RememberObserver {
    override fun onAbandoned() {
        scope.cancel()
    }

    override fun onForgotten() {
        scope.cancel()
    }

    override fun onRemembered() {
    }
}