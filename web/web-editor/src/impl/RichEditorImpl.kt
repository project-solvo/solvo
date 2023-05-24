package org.solvo.web.editor.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.atomicfu.atomic
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.document.SolvoWindow
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.editor.RichText
import org.solvo.web.ui.SolvoTopAppBar
import org.w3c.dom.Element


@Suppress("SpellCheckingInspection")
internal val editormd: dynamic = js("""editormd""")

@JsExport
internal fun onRichEditorInitialized(self: dynamic) {

}

internal object RichEditorIdGenerator {
    private val id = atomic(0)

    fun nextId(): String = "rich-text-" + id.incrementAndGet()
}

@Stable
internal class RichEditor internal constructor(
    val element: Element,
    val editor: dynamic,
) : RememberObserver {
    val isVisible: MutableState<Boolean> = mutableStateOf(false)

    private val _positionInRoot = mutableStateOf(Offset.Zero)
    val positionInRoot: State<Offset> = _positionInRoot

    private val _size = mutableStateOf(IntSize.Zero)
    val size: State<IntSize> = _size

    var isToolbarVisible: Boolean = true
        set(value) {
            if (value) {
                editor.showToolbar()
            } else {
                editor.hideToolbar()
            }
            field = value
        }

    var displayMode: RichEditorDisplayMode = RichEditorDisplayMode.EDIT_PREVIEW
        set(value) {
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
            field = value
        }

    @NoLiveLiterals
    var isInDarkTheme: Boolean = false
        set(value) {
            if (value) {
                editor.setPreviewTheme("dark")
                editor.setEditorTheme("dark")
            } else {
                editor.setPreviewTheme("default")
                editor.setEditorTheme("default")
            }
            field = value
        }


    fun setPosition(offset: Offset, density: Density) {
        _positionInRoot.value = offset
        element.asDynamic().style.marginTop = (offset.y / density.density).toString() + "px"
        element.asDynamic().style.marginLeft = (offset.x / density.density).toString() + "px"
    }

    fun setSize(size: IntSize, density: Density) {
        _size.value = size
        element.asDynamic().style.width = (size.width / density.density).toString() + "px"
        element.asDynamic().style.height = (size.height / density.density).toString() + "px"
    }

    private fun dispose() {
        document.removeChild(element)
    }

    companion object {
        @NoLiveLiterals
        fun create(
            id: String,
        ): RichEditor {
            val div = document.createElement("div")
            val element = document.createElement("div")
            element.id = id

            element.asDynamic().style.`class` = "rich-text"

            div.appendChild(element)
            document.body!!.appendChild(div)
            val editor = editormd(
                id, js(
                    """
        {
//                        width: "100%",
//                        height: "100%",
                        path : '../lib/',
                        theme : "dark",
                        previewTheme : "dark",
                        editorTheme : "pastel-on-dark",
                        markdown : "# Test",
                        codeFold : true,
                        //syncScrolling : false,
                        saveHTMLToTextarea : true,    // 保存 HTML 到 Textarea
                        searchReplace : true,
                        watch : true,                // 关闭实时预览
//                        htmlDecode : "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启    
                        toolbar  : true,             //关闭工具栏
                        previewCodeHighlight : true, // 关闭预览 HTML 的代码块高亮，默认开启
                        emoji : true,
                        taskList : true,
                        tocm            : true,         // Using [TOCM]
                        tex : true,                   // 开启科学公式TeX语言支持，默认关闭
                        flowChart : false,             // 开启流程图支持，默认关闭
                        sequenceDiagram : false,       // 开启时序/序列图支持，默认关闭,
                        //dialogLockScreen : false,   // 设置弹出层对话框不锁屏，全局通用，默认为true
                        //dialogShowMask : false,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
                        //dialogDraggable : false,    // 设置弹出层对话框不可拖动，全局通用，默认为true
                        //dialogMaskOpacity : 0.4,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
                        //dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
                        imageUpload : true,
                        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                        imageUploadURL : "./php/upload.php",
                        onload : function() {
                            console.log('onload', this);
                            //this.fullscreen();
                            //this.unwatch();
                            //this.watch().fullscreen();

                            //this.setMarkdown("#PHP");
                            //this.width("100%");
                            //this.height(480);
                            //this.resize("100%", 640);
                        },
//                        onchange: () => {}
                        lang: {
                            name: "en-us",
                        }
                    }
    """
                )
            )

            return RichEditor(div, editor)
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


internal fun main() {
    onWasmReady {
        SolvoWindow {
            Column {
                SolvoTopAppBar()
//                RichEditor(Modifier.padding(vertical = 36.dp).fillMaxSize())
//                RichText(
//                    """
//                    # Title
//                    ${'$'}${'$'}x^2${'$'}${'$'}
//                """.trimIndent()
//                )
            }
        }
    }
}