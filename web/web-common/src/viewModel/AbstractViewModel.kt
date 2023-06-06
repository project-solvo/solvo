package org.solvo.web.viewModel

import androidx.compose.runtime.RememberObserver
import kotlinx.atomicfu.atomic
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class AbstractViewModel : RememberObserver {
    private val closed = atomic(false)
    private val isClosed get() = closed.value

    private val _backgroundScope = kotlin.run {
        CoroutineScope(CoroutineExceptionHandler { _, throwable ->
            window.alert(throwable.toString())
        })
    }
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
        this.init()
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


    fun <T> Flow<T>.runningList(): Flow<List<T>> {
        return runningFold(emptyList()) { acc, value ->
            acc + value
        }
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