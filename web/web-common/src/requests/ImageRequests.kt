package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.bits.*
import org.khronos.webgl.ArrayBuffer
import org.solvo.model.api.communication.ImageUrlExchange
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageRequests(
    override val client: Client
) : Requests() {
    suspend fun postImage(
        file: File,
    ): ImageUrlExchange = client.http.post(api("/images/upload")) {
        accountAuthorization()
        contentType(ContentType.Image.Any)
        setBody(object : OutgoingContent.WriteChannelContent() {
            override suspend fun writeTo(channel: ByteWriteChannel) {
                channel.writeFully(file.readAsMemory())
            }
        })
    }.body<ImageUrlExchange>()
}

suspend fun ByteWriteChannel.writeFully(src: Memory) {
    return this.writeFully(src, 0, src.size32)
}

suspend fun File.readAsMemory(): Memory {
    val reader = FileReader()
    val buffer = suspendCoroutine { cont ->
        reader.readAsArrayBuffer(this)
        reader.onload = { event ->
            cont.resume(event.target.asDynamic().result as ArrayBuffer)
        }
    }
    return Memory.of(buffer)
}
