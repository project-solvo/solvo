package org.solvo.web

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun <T> rememberExpandablePagingState(
    pageSlice: Int,
    initialList: List<T> = emptyList(),
): ExpandablePagingState<T> = remember { ExpandablePagingStateImpl.create(initialList, pageSlice) }

@Stable
interface ExpandablePagingState<T> : PagingState<T> {
    val isExpanded: State<Boolean>

    fun expand()

    suspend fun collapse()

    suspend fun switchExpanded() {
        if (isExpanded.value) collapse() else expand()
    }
}

internal class ExpandablePagingStateImpl<T> private constructor(initialList: List<T>, pageSlice: Int) :
    ExpandablePagingState<T>,
    PagingStateImpl<T>(initialList, pageSlice) {

    override val isExpanded: MutableState<Boolean> = mutableStateOf(false)
    override val pageSlice: MutableState<Int> = mutableStateOf(pageSlice)
    private var savedSlice: Int = pageSlice
    private var savedScrollValue: Int = 0

    override fun expand() {
        if (isExpanded.value) return
        savedScrollValue = pagingContext.scrollState.value
        isExpanded.value = true
        savedSlice = pageSlice.value
        pageSlice.value = 1
        update()
    }

    override suspend fun collapse() {
        if (!isExpanded.value) return
        isExpanded.value = false
        pageSlice.value = savedSlice
        update()
        delay(200.milliseconds)
        pagingContext.scrollState.scrollTo(savedScrollValue)
    }

    companion object {
        fun <T> create(
            initialList: List<T>,
            pageSlice: Int,
        ): ExpandablePagingStateImpl<T> {
            return ExpandablePagingStateImpl(initialList, pageSlice).apply { update() }
        }
    }
}