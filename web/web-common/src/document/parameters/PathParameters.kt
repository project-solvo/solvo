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

@JsName("createPathParameters")
fun PathParameters(
    pattern: String,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
): PathParameters = PathParametersImpl(pattern, coroutineContext)

internal class PathParametersImpl(
    pattern: String,
    private val coroutineContext: CoroutineContext,
) : PathParameters {
    private lateinit var locationChangeListener: (Event) -> Unit
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


    override fun reload() {
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

        if (::locationChangeListener.isInitialized) {
            window.removeEventListener(LOCATION_CHANGE, locationChangeListener)
        }
    }

    override fun onRemembered() {
        locationChangeListener = {
            if (!disposed.value) {
                reload()
            }
        }
        window.addEventListener(LOCATION_CHANGE, locationChangeListener)
    }


    private companion object {
        @Suppress("SpellCheckingInspection")
        const val LOCATION_CHANGE = "locationchange"
    }
}

