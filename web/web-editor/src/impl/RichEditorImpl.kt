@file:OptIn(ExperimentalJsExport::class)
@file:Suppress("UnusedReceiverParameter")

package org.solvo.web.editor.impl

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.unit.*
import io.ktor.util.collections.*
import kotlinx.atomicfu.atomic
import kotlinx.browser.document
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.editor.impl.RichEditorEventBridge.listenEvents
import org.w3c.dom.Element
import org.w3c.dom.asList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration.Companion.seconds


@Suppress("SpellCheckingInspection")
internal val editormd: dynamic = js("""editormd""")
private const val RICH_TEXT_EDITORS = "rich-text-editors"

private const val RICH_TEXT_DEBUG = false

@JsExport
@JsName("onRichEditorInitialized")
fun onRichEditorInitialized(jsEditor: dynamic) {
    val id = jsEditor.id as String
    if (RICH_TEXT_DEBUG) console.log("Editor.md $id initialized")
    val editor = RichEditorIdManager.getInstanceById(id) ?: return // null if already removed
    editor.notifyLoaded()
}

@JsExport
@JsName("onRichEditorChanged")
fun onRichEditorChanged(jsEditor: dynamic) {
    val id = jsEditor.id as String
    if (RICH_TEXT_DEBUG) console.log("Editor.md $id changed")
    val editor = RichEditorIdManager.getInstanceById(id) ?: return // null if already removed
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

    fun getInstanceById(id: String): RichEditor? {
        return instances[id]
    }
}

// Load only one editor at the same time
private val editorChangedLock = Mutex()

@Stable
internal class RichEditor internal constructor(
    val id: String,
    val positionDiv: Element,
    val editor: dynamic, // editor.md object
    val isTextMode: Boolean,
) : RememberObserver {
    private val scope = CoroutineScope(SupervisorJob())
    val isVisible: MutableState<Boolean> = mutableStateOf(false)
    val displayMode: MutableState<RichEditorDisplayMode> = mutableStateOf(RichEditorDisplayMode.EDIT_PREVIEW)

    private val _positionInRoot = mutableStateOf(Offset.Zero)
    val positionInRoot: State<Offset> = _positionInRoot

    private val _size = mutableStateOf(IntSize.Zero)
    val size: State<IntSize> = _size

    private val _fontSize: MutableState<Float> = mutableStateOf(0f)

    private val _boundsInRoot = mutableStateOf(Rect.Zero)
    val boundsInRoot: State<Rect> = _boundsInRoot

    internal val editorLoaded = CompletableDeferred<Unit>()

    private var editorChanged: CompletableDeferred<Unit>? = null
    private val onEditorChanged: Channel<Unit> = Channel(onBufferOverflow = BufferOverflow.DROP_OLDEST)


    @Stable
    val actualSizeFlow: Flow<Size> = onEditorChanged.receiveAsFlow().map {
        onEditorLoaded { getActualSize() }
    }

    init {
        init()
        setEditorBounds(Rect.Zero, density = Density(1f))
        setPosition(Offset.Zero, density = Density(1f))
    }

    @NoLiveLiterals
    private fun init() {
        with(getHtmlPreviewMarkdownBody().asDynamic().style) {
            if (isTextMode) {
                padding = "0px"
            }
            backgroundColor = null
//            height = "100%"
        }
        with(getHtmlEditormdPreview().asDynamic().style) {
            if (isTextMode) {
                padding = "0px"
            }
        }
        with(getHtmlEditormdDiv().asDynamic().style) {
            margin = "0px"
        }
    }

    suspend fun setContentMarkdown(value: String) {
        onEditorLoaded {
            expectEditorChange {
                editor.setValue(value)
            }
        }
        setFontSizePx(_fontSize.value)
    }

    val isEditorLoaded get() = editorLoaded.isCompleted

    suspend fun hidePreviewCloseButton() {
        onEditorLoaded {
            repeat(10) {
                val res = document.getElementById(id)
                    ?.getElementsByClassName("editormd-preview-close-btn")
                    ?.asList()
                    ?.forEach {
                        it.asDynamic().style.display = "none"
                    }
                if (res != null) {
                    return
                } else {
                    delay(0.2.seconds) // delay a bit to fix
                }
            }
        }
    }

    internal suspend fun <R> expectEditorChange(action: suspend () -> R) {
        return editorChangedLock.withLock {
            val def = CompletableDeferred<Unit>()
            editorChanged = def
            try {
                action()
                withTimeout(5.seconds) { def.await() }
            } catch (e: Throwable) {
                editorChanged = null
                if (e is CancellationException) {
                    console.error("Timeout expectEditorChange", e)
                } else {
                    throw e
                }
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

    suspend fun setDisplayMode(value: RichEditorDisplayMode, density: Density) {
        onEditorLoaded {
            displayMode.value = value
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
            setEditorSize(size.value, density)
        }
    }

    suspend fun setShowScrollBar(show: Boolean) {
        onEditorLoaded {
            getHtmlEditormdPreview().asDynamic().style.overflow = if (show) null else "hidden"
            getHtmlPreviewMarkdownBody().asDynamic().style.overflow = if (show) null else "hidden"
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
        val marginTop = (offset.y / density.density).toString() + "px"
        val marginLeft = (offset.x / density.density).toString() + "px"
        positionDiv.asDynamic().style.marginTop = marginTop
        positionDiv.asDynamic().style.marginLeft = marginLeft
    }
//
//    init {
//        scope.launch(start = CoroutineStart.UNDISPATCHED) {
//            onEditorLoaded {
//                editor.cm.on("refresh") { cm ->
//                    scope.launch(start = CoroutineStart.UNDISPATCHED) {
//                        cmRefreshedLock.withLock {
//                            cmRefreshed?.complete(Unit)
//                        }
//                    }
//                }
//                Unit
//            }
//        }
//    }

    suspend fun setBackgroundColor(color: Color) = onEditorLoaded {
        with(getHtmlEditormdPreview().asDynamic().style) {
            backgroundColor = color.toHtmlRgbaString()
        }
        with(getHtmlPreviewMarkdownBody().asDynamic().style) {
            backgroundColor = color.toHtmlRgbaString()
        }
    }

    fun EditorLoaded.getActualSize(): Size {
        val rect = getHtmlPreviewMarkdownBody().getBoundingClientRect()
        return Size(rect.width.toFloat(), rect.height.toFloat())
    }

    suspend fun resizeToWrapPreviewContent(onClip: (size: DpSize) -> Unit) {
        onEditorLoaded {
            val rect = getHtmlPreviewMarkdownBody().getBoundingClientRect()
            setEditorSizePx(
                rect.width.toFloat(), rect.height.toFloat()
            )

            onClip(DpSize(rect.width.toFloat().dp, rect.height.toFloat().dp))
//
//            /*
//             * cm.getScrollInfo() → {left, top, width, height, clientWidth, clientHeight}
//             * Get an {left, top, width, height, clientWidth, clientHeight} object that represents the current scroll position, 
//             * the size of the scrollable area, and the size of the visible area (minus scrollbars).
//             */
//            val info = editor.cm.getScrollInfo()
//            val lineHeight = editor.cm.defaultTextHeight() as Float
//            console.log(info)
//            console.log(lineHeight)
//
//            setEditorSize(
//                IntSize(1000, ((info.height as Float) * lineHeight) as Int),
//                Density(1.0f)
//            )
        }
    }

    fun EditorLoaded.setEditorSize(size: IntSize, density: Density, force: Boolean = false) {
        if (!force && _size.value == size) return

        _size.value = size
        val widthPx = size.width / density.density
        val heightPx = size.height / density.density
        setEditorSizePx(widthPx, heightPx)
        updateDisplaySpacing(widthPx, heightPx)
    }


    private fun EditorLoaded.setEditorSizePx(widthPx: Float, heightPx: Float) {
        positionDiv.asDynamic().style.width = widthPx.toString() + "px"
        positionDiv.asDynamic().style.height = heightPx.toString() + "px"

        editor.resize()
    }

    private fun EditorLoaded.updateDisplaySpacing(widthPx: Float, heightPx: Float) {
        if (!isTextMode) { // fix spacing bug
            getHtmlPreviewMarkdownBody().let { element ->
                element.asDynamic().style.width = (widthPx / 2).toString() + "px"
                element.asDynamic().style.height = (heightPx / 2).toString() + "px"

                element.asDynamic().style.marginLeft = (widthPx / 2).toString() + "px"
            }
        }
    }

    fun setEditorBounds(bounds: Rect, density: Density) {
        println("bounds.top=${bounds.top}; positionInRoot.value.y=${positionInRoot.value.y}")
        if (_boundsInRoot.value == bounds) return
        _boundsInRoot.value = bounds

        val topPx = (bounds.top - positionInRoot.value.y) / density.density
        val leftPx = (bounds.left - positionInRoot.value.x) / density.density
        val rightPx = bounds.right / density.density + leftPx
        val bottomPx = bounds.bottom / density.density + topPx

        positionDiv.asDynamic().style.clip = "rect(${topPx}px, ${rightPx}px, ${bottomPx}px, ${leftPx}px)"
    }

    suspend fun setFontSize(size: TextUnit, density: Density) {
        val px = with(density) {
            size.value * fontScale
        }
        setFontSizePx(px)
    }

    private suspend fun setFontSizePx(px: Float) {
        if (RICH_TEXT_DEBUG) console.log("setFontSizePx px: $px")
        _fontSize.value = px
        onEditorLoaded {
            val markdownTextArea = getHtmlPreviewMarkdownBody()

            val pxText = px.toString() + "px"

            markdownTextArea.asDynamic().style.fontSize = pxText
            getHtmlCodeMirrorBody().asDynamic().style.fontSize = pxText
            getHtmlPreviewMarkdownBody().children.asList().flatMap {
                it.children.asList()
            }.forEach {
                val element = (it as? Element) ?: return@forEach
                try {
                    element.asDynamic().style.fontSize = pxText
                } catch (_: Throwable) {
                }
            }
        }
    }

    suspend fun setContentColor(color: Color) {
        onEditorLoaded {
            getHtmlPreviewMarkdownBody().asDynamic().style.color = color.toHtmlRgbaString()
            getHtmlCodeMirrorBody().asDynamic().style.color = color.toHtmlRgbaString()
        }
    }

    // class="editormd editormd-vertical editormd-theme-default"
    private fun getHtmlEditormdDiv() =
        document.querySelector("#${id}")
            ?: error("Cannot find editor.md preview content")

    // editormd preview container class="editormd-preview editormd-preview-theme-default"
    fun getHtmlEditormdPreview() =
        document.querySelector("#${id} > div.editormd-preview")
            ?: error("Cannot find editor.md preview div")

    // markdown-body
    private fun getHtmlPreviewMarkdownBody() =
        document.querySelector("#${id} > div.editormd-preview > div")
            ?: error("Cannot find editor.md preview content")

    // CodeMirror
    private fun getHtmlCodeMirrorBody() =
        document.querySelector("#${id} > div.CodeMirror")

    internal fun notifyLoaded() {
        editorLoaded.complete(Unit)
    }

    internal fun notifyChanged() {
        editorChanged?.complete(Unit)
        onEditorChanged.trySend(Unit)
    }

    private fun dispose() {
        try {
            scope.cancel()
            try {
                document.getElementById(RICH_TEXT_EDITORS)?.removeChild(positionDiv)
            } catch (_: Throwable) {
            }
            RichEditorIdManager.removeInstance(this.id)
        } catch (e: Throwable) {
            console.error("Error in RichEditor.dispose", e.stackTraceToString())
        }
    }

    companion object {
        @NoLiveLiterals
        fun create(
            id: String,
            density: Density,
            isEditable: Boolean,
            contentPadding: Dp = Dp.Unspecified,
        ): RichEditor {
            val positionDiv = document.createElement("div")
            val element = document.createElement("div")
            element.id = id

            element.asDynamic().style.`class` = "rich-text"
            positionDiv.asDynamic().style.position = "absolute"

            positionDiv.appendChild(element)
            document.getElementById(RICH_TEXT_EDITORS)?.appendChild(positionDiv)
                ?: error("Cannot find rich-text-editors to insert rich editors")

            @Suppress("UNUSED_VARIABLE") // used in js 
            val conf = mapOf(
                "delay" to if (isEditable) 300 else 0,
            )

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
                        delay: conf.delay,
                        watch : true,                // 关闭实时预览
//                        htmlDecode : "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启    
                        toolbar  : false,             //关闭工具栏
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

            editor.contentPadding =
                with(density) { contentPadding.takeOrElse { 16.dp }.toPx() }.toString() + "px" // not lively updated
            val new = RichEditor(id, positionDiv, editor, !isEditable)
            RichEditorIdManager.addInstance(id, new)
            return new
        }
    }

    suspend fun bindEvents(density: Density) {
        val list: List<Pair<String, HtmlEventCallback>>
        onEditorLoaded {
            list = listenEvents(density)
        }
        suspendCancellableCoroutine<Unit> { cont ->
            cont.invokeOnCancellation {
                list.forEach {
                    try {
                        getHtmlEditormdPreview().removeEventListener(it.first, it.second)
                    } catch (_: Throwable) {
                    }
                }
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

internal inline fun <R> RichEditor.ifEditorLoaded(action: EditorLoaded.() -> R): R? {
    return if (this.isEditorLoaded) {
        action.invoke(EditorLoaded)
    } else {
        null
    }
}

internal suspend inline fun <R> RichEditor.onEditorLoaded(action: EditorLoaded.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    editorLoaded.join()
    return action.invoke(EditorLoaded)
}

private fun Color.toHtmlRgbaString(): String {
    convert(ColorSpaces.Srgb).apply {
        return "rgba(${this.red * 255},${this.green * 255},${this.blue * 255},${this.alpha})"
    }
}

object EditorLoaded
