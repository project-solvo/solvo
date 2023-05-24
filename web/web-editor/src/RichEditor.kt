package org.solvo.web.editor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.document.SolvoWindow
import org.w3c.dom.Element


@Suppress("SpellCheckingInspection")
val editormd: dynamic = js("""editormd""")

@NoLiveLiterals
@JsName("createRichEditorState")
fun RichEditorState(
    id: String,
): RichEditorState {
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
                        }
                    }
    """
        )
    )

    return RichEditorState(div, editor)
}

@Stable
class RichEditorState internal constructor(
    private val element: Element,
    private val editor: dynamic,
) {
    val isVisible: MutableState<Boolean> = mutableStateOf(false)

    private val _positionInRoot = mutableStateOf(Offset.Zero)
    val positionInRoot: State<Offset> = _positionInRoot

    private val _size = mutableStateOf(IntSize.Zero)
    val size: State<IntSize> = _size

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
}


@Composable
fun RichEditor(
    richEditorState: RichEditorState,
    modifier: Modifier = Modifier,
) {
    Text("Sample editor")
    val density = LocalDensity.current
    Box(modifier
        .padding(32.dp)
        .border(2.dp, color = Color.Red)
        .onGloballyPositioned {
            richEditorState.setPosition(it.positionInWindow(), density)
            richEditorState.setSize(it.size, density)
        }
    ) {
        Spacer(Modifier.fillMaxSize())
    }
}

fun main() {
    val editor = RichEditorState("fuck")
    editor.isVisible.value = true
    onWasmReady {
        SolvoWindow {
            RichEditor(editor, Modifier.fillMaxSize())
        }
    }
}