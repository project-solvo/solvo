package org.solvo.web.viewModel

import androidx.compose.runtime.RememberObserver
import kotlinx.atomicfu.atomic
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class AbstractViewModel : RememberObserver {
    private val closed = atomic(false)
    private val isClosed get() = closed.value

    private val _backgroundScope = lazy {
        CoroutineScope(CoroutineExceptionHandler { _, throwable ->
            window.alert(throwable.toString())
        })
    }
    val backgroundScope: CoroutineScope
        get() {
            if (isClosed) throw IllegalStateException("ViewModel is already closed")
            return _backgroundScope.value
        }


    final override fun onAbandoned() {
        if (!closed.compareAndSet(expect = false, update = true)) {
            return
        }
        if (_backgroundScope.isInitialized()) {
            backgroundScope.cancel()
        }
    }

    final override fun onForgotten() {
        if (!closed.compareAndSet(expect = false, update = true)) {
            return
        }
        if (_backgroundScope.isInitialized()) {
            backgroundScope.cancel()
        }
    }

    final override fun onRemembered() {
        this.init()
    }

    protected open fun init() {
    }

    fun <T> Flow<T>.shareInBackground(
        started: SharingStarted = SharingStarted.Eagerly,
        replay: Int = 1
    ): SharedFlow<T> = shareIn(backgroundScope, started, replay)
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