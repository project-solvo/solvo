package org.solvo.web.viewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface LiveStorageContext<T> {
    var currentData: T
}

fun <E, T> CoroutineScope.liveStorage(
    initialValue: T,
    dataFlow: Flow<T>,
    eventFlow: Flow<E>,
    eventHandler: LiveStorageContext<T>.(E) -> Unit,
): StateFlow<T> {
    val data = MutableStateFlow(initialValue)
    launch {
        dataFlow.collect {
            data.emit(it)
        }
    }
    val context = object : LiveStorageContext<T> {
        override var currentData: T
            get() = data.value
            set(value) {
                data.value = value
            }
    }
    launch {
        eventFlow.collect {
            eventHandler.invoke(context, it)
        }
    }
    return data
}
