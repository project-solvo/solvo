package org.solvo.web.document.parameters

import androidx.compose.runtime.*
import kotlinx.atomicfu.atomic
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.solvo.model.api.WebPagePathPatterns
import org.w3c.dom.events.Event
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface PathParameters : RememberObserver {
    var pattern: String
    fun reload()

    fun registerEventListeners()

    @Stable
    val allParameters: State<Map<String, String>>

    @Stable
    fun argumentNullable(name: String): StateFlow<String?>

    @Stable
    fun argument(name: String): StateFlow<String>

    fun dispose()
}

/**
 * See [WebPagePathPatterns] for possible names
 */
operator fun PathParameters.get(name: String): String? = allParameters.value[name]

/**
 * @see WebPagePathPatterns
 */
@JsName("createPathParameters")
fun PathParameters(
    pattern: String,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
): PathParameters = PathParametersImpl(pattern, coroutineContext).apply {
    registerEventListeners()
}

internal class PathParametersImpl internal constructor(
    pattern: String,
    private val coroutineContext: CoroutineContext,
) : PathParameters {
    private var locationChangeListener: ((Event) -> Unit)? = null
    override var pattern: String = pattern
        set(value) {
            field = value
            reload()
        }

    private val _allParameters: MutableState<Map<String, String>> = mutableStateOf(mapOf())
    override val allParameters: State<Map<String, String>> get() = _allParameters

    private val observedParams = mutableMapOf<String, MutableStateFlow<String?>>() // can implement with derived states
    private val observedParamsNotNull =
        mutableMapOf<String, MutableStateFlow<String>>() // can implement with derived states

    private var currentPathValue: String? = null

    override fun reload() {
        if (currentPathValue == window.location.pathname) return // same

        val map = PathParameterParser.parse(pattern, window.location.pathname)
        console.log(
            "Path parameters: ${
                map.entries.joinToString(
                    prefix = "{",
                    postfix = "}"
                )
            }"
        )

        _allParameters.value = map

        // Update observed values
        for (observedParam in observedParams) {
            val newValue = map[observedParam.key]
            if (newValue != null) {
                observedParam.value.value = newValue
            }
        }
        for (observedParam in observedParamsNotNull) {
            val newValue = map[observedParam.key]
            if (newValue != null) {
                observedParam.value.value = newValue
            }
        }
    }

    override fun registerEventListeners() {
        if (disposed.value) return
        if (locationChangeListener != null) return

        locationChangeListener = {
            if (!disposed.value) {
                reload()
            }
        }
        window.addEventListener(WindowEvents.EVENT_LOCATION_CHANGE, locationChangeListener)
        window.addEventListener(WindowEvents.EVENT_POP_STATE, locationChangeListener)
        window.addEventListener(WindowEvents.EVENT_PUSH_STATE, locationChangeListener)
    }

    override fun argumentNullable(name: String): StateFlow<String?> {
        return observedParams.getOrPut(name) {
            MutableStateFlow(null)
        }
    }

    init {
        reload()
    }

    override fun argument(name: String): StateFlow<String> {
        return observedParamsNotNull.getOrPut(name) {
            MutableStateFlow(
                get(name)
                    ?: error(
                        "Cannot find path parameter '$name'. pattern=$pattern. parsedPrams=${
                            allParameters.value.entries.joinToString(
                                prefix = "{",
                                postfix = "}"
                            )
                        }"
                    )
            )
        }
    }

    override fun onAbandoned() {
        dispose()
    }

    override fun onForgotten() {
        dispose()
    }


    private val disposed = atomic(false)
    override fun dispose() {
        if (!disposed.compareAndSet(expect = false, update = true)) return

        locationChangeListener?.let { locationChangeListener ->
            window.removeEventListener(WindowEvents.EVENT_LOCATION_CHANGE, locationChangeListener)
            window.removeEventListener(WindowEvents.EVENT_POP_STATE, locationChangeListener)
            window.removeEventListener(WindowEvents.EVENT_PUSH_STATE, locationChangeListener)
        }
    }

    override fun onRemembered() {
        registerEventListeners()
    }
}

