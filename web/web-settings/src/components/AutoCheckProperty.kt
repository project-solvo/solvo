package org.solvo.web.settings.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
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
    val value get() = valueFlow.value

    fun setValue(value: T)

    val error: StateFlow<E?> // null: debouncing or no error
    val isChecking: StateFlow<Boolean>
    val hasError: StateFlow<Boolean?> // null: debouncing or checking
}


@JsName("createAutoCheckProperty")
fun <T, E> AutoCheckProperty(
    coroutineScope: CoroutineScope,
    mergeValues: StateFlow<T>,
    debounce: Duration = 1.seconds,
    transformValue: (T) -> T = { it },
    checkError: ErrorChecker<T, E>,
): AutoCheckProperty<T, E> = AutoCheckPropertyImpl(coroutineScope, mergeValues, debounce, transformValue, checkError)

@JsName("createAutoCheckProperty2")
fun <T, E> AbstractViewModel.AutoCheckProperty(
    mergeValues: StateFlow<T>,
    debounce: Duration = 1.seconds,
    transformValue: (T) -> T = { it },
    checkError: ErrorChecker<T, E>,
): AutoCheckProperty<T, E> = AutoCheckProperty(backgroundScope, mergeValues, debounce, transformValue, checkError)

class AutoCheckPropertyImpl<T, E>(
    private val scope: CoroutineScope,
    mergeFrom: StateFlow<T>,
    debounce: Duration,
    private val transformValue: (T) -> T,
    checkError: ErrorChecker<T, E>,
) : AutoCheckProperty<T, E> {
    private val localValueFlow: MutableStateFlow<T> = MutableStateFlow(mergeFrom.value)

    override val valueFlow: StateFlow<T> =
        merge(localValueFlow, mergeFrom)
            .stateIn(scope, started = SharingStarted.Eagerly, mergeFrom.value)

    override fun setValue(value: T) {
        this.localValueFlow.value = transformValue(value)
    }

    private var currentErrorChecker: Job? = null

    //    override val isAvailable: MutableStateFlow<Availability> = MutableStateFlow(Availability.AVAILABLE)
    private val checker: StateFlow<Deferred<E?>?> = valueFlow.debounce(debounce)
        .map { item ->
            currentErrorChecker?.cancel()
            scope.async {
                checkError(item).also {
                    error.emit(it)
                }
            }.also {
                currentErrorChecker = it
            }
        }
        .stateIn(scope, started = SharingStarted.Eagerly, null)

    override val error: MutableStateFlow<E?> = MutableStateFlow(null)

    override val isChecking: StateFlow<Boolean> = checker.map {
        it != null && it.isActive
    }.stateIn(scope, started = SharingStarted.Eagerly, false)

    override val hasError: StateFlow<Boolean?> = error.map {
        it != null
    }.stateIn(scope, started = SharingStarted.Eagerly, null)

    private fun <T> Flow<T>.stateInScope(
        scope: CoroutineScope = this@AutoCheckPropertyImpl.scope
    ): StateFlow<T?> =
        stateIn(scope, started = SharingStarted.Eagerly, null)
}