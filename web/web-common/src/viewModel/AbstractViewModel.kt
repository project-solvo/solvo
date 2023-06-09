package org.solvo.web.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.solvo.model.foundation.Uuid
import org.solvo.web.utils.byWindowAlert
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class AbstractViewModel : RememberObserver {
    private val closed = atomic(false)
    private val isClosed get() = closed.value

    private var _backgroundScope = createBackgroundScope()
    val backgroundScope: CoroutineScope
        get() {
            return _backgroundScope
        }


    final override fun onAbandoned() {
        console.log("${this::class.simpleName} onAbandoned")
        dispose()
    }

    private fun dispose() {
        if (!closed.compareAndSet(expect = false, update = true)) {
            return
        }
//        if (_backgroundScope.isInitialized()) {
        backgroundScope.cancel()
//        }
    }

    final override fun onForgotten() {
        console.log("${this::class.simpleName} onForgotten")
        dispose()
    }

    final override fun onRemembered() {
        console.log("${this::class.simpleName} onRemembered")
        if (!_backgroundScope.isActive) {
            _backgroundScope = createBackgroundScope()
        }
        this.init()
    }

    private fun createBackgroundScope(): CoroutineScope {
        return CoroutineScope(CoroutineExceptionHandler.byWindowAlert())
    }

    /**
     * Called when the view model is remembered.
     */
    protected open fun init() {
    }

    fun <T> Flow<T>.shareInBackground(
        started: SharingStarted = SharingStarted.Eagerly,
        replay: Int = 1
    ): SharedFlow<T> = shareIn(backgroundScope, started, replay)

    fun <T> Flow<T>.stateInBackground(
        started: SharingStarted = SharingStarted.Eagerly,
        initialValue: T,
    ): StateFlow<T> = stateIn(backgroundScope, started, initialValue)

    fun <T> Flow<T>.stateInBackground(
        started: SharingStarted = SharingStarted.Eagerly,
    ): StateFlow<T?> = stateIn(backgroundScope, started, null)


    fun <T> Flow<T>.runningList(): Flow<List<T>> {
        return runningFold(emptyList()) { acc, value ->
            acc + value
        }
    }

    fun <T> CoroutineScope.load(uuid: Uuid, calc: suspend () -> T?): LoadingUuidItem<T> {
        val flow = MutableStateFlow<T?>(null)
        launch {
            flow.value = calc()
        }
        return LoadingUuidItem(uuid, flow)
    }

    fun <T, R> Flow<T>.mapLatestSupervised(transform: suspend CoroutineScope.(value: T) -> R): Flow<R> =
        mapLatest {
            supervisorScope { transform(it) }
        }

    inline fun <T> List<Uuid>.mapLoadIn(
        scope: CoroutineScope,
        crossinline calc: suspend (Uuid) -> T?
    ): List<LoadingUuidItem<T>> {
        return map { scope.load(it) { calc(it) } }
    }
}


fun <V : AbstractViewModel> V.launchInBackground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend V.() -> Unit
): Job {
    return backgroundScope.launch(context, start) {
        block()
    }
}

fun <V : AbstractViewModel> V.launchInBackgroundAnimated(
    isLoadingState: MutableState<Boolean>,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend V.() -> Unit
): Job {
    isLoadingState.value = true
    return backgroundScope.launch(context, start) {
        block()
        isLoadingState.value = false
    }
}
