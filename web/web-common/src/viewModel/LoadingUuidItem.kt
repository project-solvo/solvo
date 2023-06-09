package org.solvo.web.viewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.StateFlow
import org.solvo.model.foundation.Uuid
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun <T> LoadingUuidItem(uuid: Uuid, ready: T?): LoadingUuidItem<T> =
    ComputedLoadingUuidItem(uuid, ready)

fun <T> LoadingUuidItem(uuid: Uuid, ready: StateFlow<T?>): LoadingUuidItem<T> =
    StateFlowLoadingUuidItem(uuid, ready)

interface LoadingUuidItem<T> {
    @Composable
    fun collectAsState(
        context: CoroutineContext
    ): State<T?>

}

@Suppress("NOTHING_TO_INLINE") // for compose, inlining matters
@Composable
inline fun <T> LoadingUuidItem<T>.collectAsState(): State<T?> = collectAsState(EmptyCoroutineContext)

internal class ComputedLoadingUuidItem<T>(
    val id: Uuid,
    val ready: T?
) : LoadingUuidItem<T> {
    @Composable
    override fun collectAsState(context: CoroutineContext): State<T?> = remember { mutableStateOf(ready) }
}

internal class StateFlowLoadingUuidItem<T>(
    val id: Uuid,
    val flow: StateFlow<T?>,
) : LoadingUuidItem<T> {
    @Composable
    override fun collectAsState(context: CoroutineContext): State<T?> = this.flow.collectAsState(context)
}

