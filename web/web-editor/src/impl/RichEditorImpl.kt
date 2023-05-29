@file:OptIn(ExperimentalJsExport::class)

package org.solvo.web.editor.impl

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import io.ktor.util.collections.*
import kotlinx.atomicfu.atomic
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.ui.WindowState
import org.w3c.dom.Element
import org.w3c.dom.events.WheelEvent
import kotlin.time.Duration.Companion.seconds


@Suppress("SpellCheckingInspection")
internal val editormd: dynamic = js("""editormd""")

@JsExport
@JsName("onRichEditorInitialized")
fun onRichEditorInitialized(jsEditor: dynamic) {
    val id = jsEditor.id as String
    console.log("Editor.md $id initialized")
    val editor = RichEditorIdManager.getInstanceById(id)
    editor.notifyLoaded()
}

@JsExport
@JsName("onRichEditorChanged")
fun onRichEditorChanged(jsEditor: dynamic) {
    val id = jsEditor.id as String
    console.log("Editor.md $id changed")
    val editor = RichEditorIdManager.getInstanceById(id)
    editor.notifyChanged()
}

internal object RichEditorIdManager {
    private val id = atomic(0)
    private val instances = ConcurrentMap<String, RichEditor>()

    fun nextId(): String = "rich-text-" + id.incrementAndGet()

    fun removeInstance(id: String) {
        instances.remove(id)
    }

    fun addInstance(id: String, richEditor: RichEditor) {
        instances[id] = richEditor
    }

    fun getInstanceById(id: String): RichEditor {
        return instances[id] ?: error("Could not find RichEditor instance with id '$id'")
    }
}

// Load only one editor at the same time
private val editorChangedLock = Mutex()

@Stable
internal class RichEditor internal constructor(
    val id: String,
    val positionDiv: Element,
    val clipDiv: Element,
    val editor: dynamic, // editor.md object
) : RememberObserver {
    val isVisible: MutableState<Boolean> = mutableStateOf(false)

    private val _positionInRoot = mutableStateOf(Offset.Zero)
    val positionInRoot: State<Offset> = _positionInRoot

    private val _size = mutableStateOf(IntSize.Zero)
    val size: State<IntSize> = _size

    private val _boundsInRoot = mutableStateOf(Rect.Zero)
    val boundsInRoot: State<Rect> = _boundsInRoot

    private val editorLoaded = CompletableDeferred<Unit>()

    private var editorChanged: CompletableDeferred<Unit>? = null

    internal suspend inline fun <R> onEditorLoaded(action: () -> R): R {
        editorLoaded.join()
        return action()
    }

    internal suspend fun <R> expectEditorChange(action: suspend () -> R) {
        return editorChangedLock.withLock {
            val def = CompletableDeferred<Unit>()
            editorChanged = def
            try {
                action()
                withTimeout(2.seconds) { def.await() }
            } catch (e: Throwable) {
                editorChanged = null
                if (e !is CancellationException) throw e
            }
        }
    }

    suspend fun setToolbarVisible(value: Boolean) {
        onEditorLoaded {
            if (value) {
                editor.showToolbar()
            } else {
                editor.hideToolbar()
            }
        }
    }

    suspend fun setDisplayMode(value: RichEditorDisplayMode) {
        onEditorLoaded {
            when (value) {
                RichEditorDisplayMode.PREVIEW_ONLY -> {
                    editor.previewing()
                }

                RichEditorDisplayMode.EDIT_ONLY -> {
                    editor.unwatch()
                }

                RichEditorDisplayMode.EDIT_PREVIEW -> {
                    editor.watch()
                }

                RichEditorDisplayMode.NONE -> {
                    editor.hide()
                }
            }
        }
    }

    @NoLiveLiterals
    suspend fun setInDarkTheme(value: Boolean) {
        onEditorLoaded {
            if (value) {
                editor.setPreviewTheme("dark")
                editor.setEditorTheme("darcula")
                editor.setTheme("dark")
            } else {
                editor.setPreviewTheme("default")
                editor.setEditorTheme("default")
                editor.setTheme("default")
            }
        }
    }


    fun setPosition(offset: Offset, density: Density) {
        if (_positionInRoot.value == offset) return

        _positionInRoot.value = offset
        positionDiv.asDynamic().style.marginTop = (offset.y / density.density).toString() + "px"
        positionDiv.asDynamic().style.marginLeft = (offset.x / density.density).toString() + "px"
    }

    suspend fun setEditorSize(size: IntSize, density: Density) {
        if (_size.value == size) return

        _size.value = size
        val widthPx = size.width / density.density
        val heightPx = size.height / density.density
        positionDiv.asDynamic().style.width = widthPx.toString() + "px"
        positionDiv.asDynamic().style.height = heightPx.toString() + "px"
        clipDiv.asDynamic().style.width = widthPx.toString() + "px"
        clipDiv.asDynamic().style.height = heightPx.toString() + "px"
        onEditorLoaded {
            editor.resize()
        }
    }

    fun setEditorBounds(bounds: Rect, density: Density) {
        if (_boundsInRoot.value == bounds) return
        _boundsInRoot.value = bounds

        val topPx = bounds.top / density.density
        val rightPx = bounds.right / density.density
        val bottomPx = bounds.bottom / density.density
        val leftPx = bounds.left / density.density

        clipDiv.asDynamic().style.clip = "rect(${topPx}px, ${rightPx}px, ${bottomPx}px, ${leftPx}px)"
    }

    suspend fun setFontSize(size: TextUnit, density: Density) {
        onEditorLoaded {
            val px =
                with(density) { (size / 2).toPx() } // I don't know why, but `/ 2` makes it more close to normal Compose font size 
            val markdownTextArea =
                getHtmlPreviewContent()
            markdownTextArea.asDynamic().style.fontSize = px.toString() + "px"
        }
    }

    private fun getHtmlPreviewDiv() =
        document.querySelector("#${id} > div.editormd-preview")
            ?: error("Cannot find editor.md preview div")

    private fun getHtmlPreviewContent() =
        document.querySelector("#${id} > div.editormd-preview > div")
            ?: error("Cannot find editor.md preview content")

    internal fun notifyLoaded() {
        editorLoaded.complete(Unit)
    }

    internal fun notifyChanged() {
        editorChanged?.complete(Unit)
    }

    private fun dispose() {
        try {
            document.removeChild(positionDiv)
            RichEditorIdManager.removeInstance(this.id)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    companion object {
        @NoLiveLiterals
        fun create(
            id: String,
            windowState: WindowState,
        ): RichEditor {
            val clipDiv = document.createElement("div")
            val positionDiv = document.createElement("div")
            val element = document.createElement("div")
            element.id = id

            element.asDynamic().style.`class` = "rich-text"
            positionDiv.asDynamic().style.position = "absolute"
            clipDiv.asDynamic().style.position = "absolute"

            positionDiv.appendChild(element)
            clipDiv.appendChild(positionDiv)
            document.body?.appendChild(clipDiv) ?: error("Document body is null")

            val editor = editormd(
                id, js(
                    """
        {
                        width: "100%",
                        height: "100%",
                        path : '/editor.md/lib/',
                        theme : "default",
                        previewTheme : "default",
                        editorTheme : "default",
                        markdown : "",
                        codeFold : true,
                        syncScrolling : true,
                        saveHTMLToTextarea : true,    // 保存 HTML 到 Textarea
                        searchReplace : true,
                        autoLoadModules: true,
                        watch : true,                // 关闭实时预览
//                        htmlDecode : "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启    
                        toolbar  : true,             //关闭工具栏
                        previewCodeHighlight : true, // 关闭预览 HTML 的代码块高亮，默认开启
                        emoji : true,
                        taskList : true,
                        tocm            : true,         // Using [TOCM]
                        tex : true,                   // 开启科学公式TeX语言支持，默认关闭
                        flowChart : true,             // 开启流程图支持，默认关闭
                        sequenceDiagram : true,       // 开启时序/序列图支持，默认关闭,
                        //dialogLockScreen : false,   // 设置弹出层对话框不锁屏，全局通用，默认为true
                        //dialogShowMask : false,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
                        //dialogDraggable : false,    // 设置弹出层对话框不可拖动，全局通用，默认为true
                        //dialogMaskOpacity : 0.4,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
                        //dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
                        imageUpload : true,
                        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                        imageUploadURL : "./php/upload.php",
                        onload : function() {
                            require('./web-editor').org.solvo.web.editor.impl.onRichEditorInitialized(this);
                            //this.fullscreen();
                            //this.unwatch();
                            //this.watch().fullscreen();

                            //this.setMarkdown("#PHP");
                            //this.width("100%");
                            //this.height(480);
                            //this.resize("100%", 640);
                        },
                        onchange : function() {
                            require('./web-editor').org.solvo.web.editor.impl.onRichEditorChanged(this);
                        }
                    }
    """
                )
            )

            val new = RichEditor(id, positionDiv, clipDiv, editor)
            RichEditorIdManager.addInstance(id, new)
            return new
        }
    }

    suspend fun onScroll(density: Density, onScroll: (Offset) -> Unit) {
        onEditorLoaded {
            getHtmlPreviewDiv().addEventListener("mousewheel", { event ->
                event as WheelEvent
                onScroll(
                    Offset(
                        event.deltaX.toFloat() / density.density,
                        event.deltaY.toFloat() / density.density,
                    )
                )
                //                windowState.skikoView?.onPointerEvent(toSkikoScrollEvent(event as WheelEvent))
            })
        }
        suspendCancellableCoroutine<Unit> { cont ->
            cont.invokeOnCancellation {

            }
        }
    }

    override fun onAbandoned() {
        dispose()
    }

    override fun onForgotten() {
        dispose()
    }

    override fun onRemembered() {
    }
}


