package org.solvo.web.ui

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun getUrl(url: String): ByteArray {
    return suspendCoroutine { continuation ->
        val req = XMLHttpRequest()
        req.open("GET", url, true)
        req.responseType = XMLHttpRequestResponseType.ARRAYBUFFER

        req.onload = {
            val arrayBuffer = req.response
            if (arrayBuffer is ArrayBuffer) {
                continuation.resume(arrayBuffer.toByteArray())
            } else {
                continuation.resumeWithException(MissingResourceException(url))
            }
        }
        req.send(null)
    }
}

private fun ArrayBuffer.toByteArray() = Int8Array(this, 0, byteLength).unsafeCast<ByteArray>()

internal class MissingResourceException constructor(path: String) :
    Exception("Missing resource with path: $path")
