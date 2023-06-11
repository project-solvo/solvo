package org.solvo.web.ui.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration

@Composable
fun <T : Any> rememberMutableDebouncedState(
    initial: T,
    debounce: Duration,
): MutableState<T> {
    val richTextHasVisualOverflowFlow = remember { MutableStateFlow(initial) }
    val state = remember(debounce) { richTextHasVisualOverflowFlow.debounce(debounce) }.collectAsState(initial)
    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    richTextHasVisualOverflowFlow.value = value
                }

            override fun component1(): T = value

            private var component2: ((T) -> Unit)? = null
            override fun component2(): (T) -> Unit {
                component2?.let { return it }

                val lambda = fun(newValue: T) { value = newValue }
                component2 = lambda
                return lambda
            }

        }
    }
}
