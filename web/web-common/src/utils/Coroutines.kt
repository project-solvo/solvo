package org.solvo.web.utils

import kotlinx.browser.window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler

fun CoroutineExceptionHandler.Key.byWindowAlert() = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    if (throwable is CancellationException || throwable.message?.contains("Fail to fetch") == true) {
        return@CoroutineExceptionHandler
    }
    window.alert(throwable.stackTraceToString())
}