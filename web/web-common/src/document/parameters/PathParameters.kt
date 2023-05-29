package org.solvo.web.document.parameters

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.solvo.model.api.WebPagePathPatterns
import org.w3c.dom.events.Event

interface PathParameters : RememberObserver {
    var pattern: String
    fun reload()

    @Stable
    val allParameters: State<Map<String, String>>

    @Stable
    fun paramNullable(name: String): State<String?>

    @Stable
    fun param(name: String): State<String>
}

/**
 * See [WebPagePathPatterns] for possible names
 */
operator fun PathParameters.get(name: String): String? = allParameters.value[name]

@JsName("createPathParameters")
fun PathParameters(
    pattern: String,
): PathParameters = PathParametersImpl(pattern)

internal class PathParametersImpl(
    pattern: String,
) : PathParameters {
    private lateinit var locationChangeListener: (Event) -> Unit
    override var pattern: String = pattern
        set(value) {
            field = value
            reload()
        }

    private val _allParameters: MutableState<Map<String, String>> = mutableStateOf(mapOf())
    override val allParameters: State<Map<String, String>> get() = _allParameters

    private val observedParams = mutableMapOf<String, MutableState<String?>>() // can implement with derived states
    private val observedParamsNotNull =
        mutableMapOf<String, MutableState<String>>() // can implement with derived states


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

    override fun paramNullable(name: String): State<String?> {
        return observedParams.getOrPut(name) {
            mutableStateOf(null)
        }
    }

    init {
        reload()
    }

    override fun param(name: String): State<String> {
        return observedParamsNotNull.getOrPut(name) {
            mutableStateOf(
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
        if (::locationChangeListener.isInitialized) {
            window.removeEventListener(LOCATION_CHANGE, locationChangeListener)
        }
    }

    override fun onForgotten() {
        if (::locationChangeListener.isInitialized) {
            window.removeEventListener(LOCATION_CHANGE, locationChangeListener)
        }
    }

    override fun onRemembered() {
        locationChangeListener = {
            reload()
        }
        window.addEventListener(LOCATION_CHANGE, locationChangeListener)
    }


    private companion object {
        @Suppress("SpellCheckingInspection")
        const val LOCATION_CHANGE = "locationchange"
    }
}

