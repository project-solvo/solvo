package org.solvo.web.utils

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineExceptionHandler

fun CoroutineExceptionHandler.Key.byWindowAlert() = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    window.alert(throwable.toString())
}