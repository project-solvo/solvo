package org.solvo.web.ui.image

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.skia.Image
import org.solvo.web.requests.client

@Composable
fun rememberImagePainter(
    url: String?,
    default: Painter? = null,
    error: Painter? = null,
    onError: (suspend (Throwable) -> Unit)? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
): Painter {
    var painter: Painter? by remember { mutableStateOf(default) }

    LaunchedEffect(url) {
        try {
            painter = if (url == null) {
                default
            } else {
                val data = client.http.get(url).readBytes()
                val bitmap =
                    Image.makeFromEncoded(data).toComposeImageBitmap()

                BitmapPainter(bitmap, filterQuality = filterQuality)
            }
        } catch (e: Throwable) {
            console.error("Failed loading image", e)
            if (error != null) {
                painter = error
            }
            onError?.invoke(e)
        }
    }

    return painter ?: NoOpPainter
}

@Composable
fun rememberImagePainter(
    url: String?,
    default: ImageVector? = null,
    error: ImageVector? = null,
    onError: (suspend (Throwable) -> Unit)? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
): Painter {
    val defaultPainter: Painter? = if (default == null) null else rememberVectorPainter(default)
    val onErrorPainter: Painter? = if (error == null) null else rememberVectorPainter(error)
    return rememberImagePainter(url, defaultPainter, onErrorPainter, onError, filterQuality)
}

object NoOpPainter : Painter() {
    override val intrinsicSize: Size get() = Size.Unspecified
    override fun DrawScope.onDraw() {
    }
}