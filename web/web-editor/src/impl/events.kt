package org.solvo.web.editor.impl

import androidx.compose.ui.unit.Density
import org.jetbrains.skiko.*
import org.solvo.web.ui.WindowState
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent


typealias HtmlEventCallback = ((Event) -> Unit)?

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
object RichEditorEventBridge {

    internal fun RichEditor.listenEvents(
        density: Density,
    ): List<Pair<String, HtmlEventCallback>> {
        val list = mutableListOf<Pair<String, HtmlEventCallback>>()
        fun callback(name: String, block: HtmlEventCallback): HtmlEventCallback {
            list.add(name to block)
            return block
        }

        val skia = WindowState.current.skiaLayer

        with(skia) {
            val htmlEditormdPreview = getHtmlEditormdDiv()
            with(RichEditorEventBridgeContext(this@listenEvents, density)) {
                // mousedown can cause compose bug

//                htmlEditormdPreview.addEventListener("mousedown", { event ->
//                    event as MouseEvent
//                    isPointerPressed = true
//                    skikoView?.onPointerEvent(toSkikoEventAdjusted(event, SkikoPointerEventKind.DOWN))
//                })
                htmlEditormdPreview.addEventListener("mouseup", { event ->
                    event as MouseEvent
                    isPointerPressed = false
                    skikoView?.onPointerEvent(toSkikoEventAdjusted(event, SkikoPointerEventKind.UP))
                })
                htmlEditormdPreview.addEventListener("mousemove", { event ->
                    event as MouseEvent
                    if (isPointerPressed) {
                        skikoView?.onPointerEvent(toSkikoDragEventAdjusted(event))
                    } else {
                        skikoView?.onPointerEvent(toSkikoEventAdjusted(event, SkikoPointerEventKind.MOVE))
                    }
                })
                htmlEditormdPreview.addEventListener("mousewheel", callback("mousewheel") { event ->
                    event as WheelEvent
//                    onScroll(
//                        Offset(
//                            event.deltaX.toFloat() / density.density,
//                            event.deltaY.toFloat() / density.density,
//                        )
//                    )
                    skikoView?.onPointerEvent(toSkikoScrollEventAdjusted(event))
                })
            }
        }

        return list
    }

    private class RichEditorEventBridgeContext(
        private val editor: RichEditor,
        private val density: Density,
    ) {
        private val xInRoot get() = editor.positionInRoot.value.x / density.density
        private val yInRoot get() = editor.positionInRoot.value.y / density.density

        fun toSkikoScrollEventAdjusted(
            event: WheelEvent,
        ): SkikoPointerEvent {
            return toSkikoScrollEvent(event).run {
                copy(
                    x = x + xInRoot,
                    y = y + yInRoot,
                )
            }
        }

        fun toSkikoDragEventAdjusted(
            event: MouseEvent,
        ): SkikoPointerEvent {
            return toSkikoDragEvent(event).run {
                copy(
                    x = x + xInRoot,
                    y = y + yInRoot,
                )
            }
        }

        fun toSkikoEventAdjusted(
            event: MouseEvent,
            kind: SkikoPointerEventKind
        ): SkikoPointerEvent {
            return toSkikoEvent(event, kind).run {
                copy(
                    x = x + xInRoot,
                    y = y + yInRoot,
                )
            }
        }
    }
}