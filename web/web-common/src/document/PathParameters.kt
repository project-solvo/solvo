package org.solvo.web.document

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.solvo.model.api.WebPagePatterns
import org.w3c.dom.events.Event

interface PathParameters : RememberObserver {
    var pattern: String
    fun reload()

    @Stable
    val allParameters: State<Map<String, String>>

    @Stable
    fun param(name: String): State<String?>
}

/**
 * See [WebPagePatterns] for possible names
 */
operator fun PathParameters.get(name: String): String? = allParameters.value[name]

@JsName("createPathParameters")
fun PathParameters(
    pattern: String,
): PathParameters = PathParametersImpl(pattern)

@Composable
fun rememberPathParameters(
    pattern: String,
): PathParameters {
    val parameters: PathParameters = remember(pattern) { PathParameters(pattern) }
    LaunchedEffect(pattern) {
        parameters.pattern = pattern
    }
    return parameters
}

internal class PathParametersImpl(
    pattern: String,
) : PathParameters {
    private lateinit var locationChangeListener: (Event) -> Unit
    override var pattern: String = pattern
        set(value) {
            field = value
            reload()
        }

    override fun reload() {
        val map = PathParameterParser.parse(pattern, window.location.href)
        _allParameters.value = map

        // Update observed values
        for (observedParam in observedParams) {
            val newValue = map[observedParam.key]
            if (newValue != null) {
                observedParam.value.value = newValue
            }
        }
    }

    private val _allParameters: MutableState<Map<String, String>> = mutableStateOf(mapOf())
    override val allParameters: State<Map<String, String>> get() = _allParameters
    private val observedParams = mutableMapOf<String, MutableState<String?>>() // can implement with derived states

    override fun param(name: String): State<String?> {
        return observedParams.getOrPut(name) {
            mutableStateOf(null)
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

