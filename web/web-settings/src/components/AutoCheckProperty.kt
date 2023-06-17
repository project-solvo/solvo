package org.solvo.web.settings.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.web.viewModel.AbstractViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

//typealias ErrorComputation<E> = suspend () -> E?
typealias ErrorChecker<T, E> = suspend (T) -> E?

@Stable
interface AutoCheckProperty<T, E> {
    val valueFlow: StateFlow<T>
    fun setValue(value: T)

    val error: StateFlow<E?> // null: debouncing or no error
    val isChecking: StateFlow<Boolean>
    val hasError: StateFlow<Boolean?> // null: debouncing or checking
}


@JsName("createAutoCheckProperty")
fun <T, E> AutoCheckProperty(
    coroutineScope: CoroutineScope,
    initial: T,
    debounce: Duration = 1.seconds,
    transformValue: (T) -> T = { it },
    checkError: ErrorChecker<T, E>,
): AutoCheckProperty<T, E> = AutoCheckPropertyImpl(coroutineScope, initial, debounce, transformValue, checkError)

@JsName("createAutoCheckProperty2")
fun <T, E> AbstractViewModel.AutoCheckProperty(
    initial: T,
    debounce: Duration = 1.seconds,
    transformValue: (T) -> T = { it },
    checkError: ErrorChecker<T, E>,
): AutoCheckProperty<T, E> = AutoCheckProperty(backgroundScope, initial, debounce, transformValue, checkError)

class AutoCheckPropertyImpl<T, E>(
    private val scope: CoroutineScope,
    initial: T,
    debounce: Duration,
    private val transformValue: (T) -> T,
    checkError: ErrorChecker<T, E>,
) : AutoCheckProperty<T, E> {
    override val valueFlow: MutableStateFlow<T> = MutableStateFlow(initial)

    override fun setValue(value: T) {
        this.valueFlow.value = transformValue(value)
    }

    //    override val isAvailable: MutableStateFlow<Availability> = MutableStateFlow(Availability.AVAILABLE)
    private val checker: StateFlow<Deferred<E?>?> = valueFlow.debounce(debounce)
        .mapLatest { item -> scope.async { checkError(item) } }
        .stateIn(scope, started = SharingStarted.Eagerly, null)

    override val error: StateFlow<E?> = valueFlow.debounce(debounce)
        .mapLatest { item ->
            checkError(item)
        }
        .stateInScope()

    override val isChecking: StateFlow<Boolean> = checker.map {
        it != null && it.isActive
    }.stateIn(scope, started = SharingStarted.Eagerly, false)

    override val hasError: StateFlow<Boolean?> = checker.map {
        it != null && it.isCompleted && it.getCompleted() != null
    }.stateIn(scope, started = SharingStarted.Eagerly, null)

    private fun <T> Flow<T>.stateInScope(
        scope: CoroutineScope = this@AutoCheckPropertyImpl.scope
    ): StateFlow<T?> =
        stateIn(scope, started = SharingStarted.Eagerly, null)
}