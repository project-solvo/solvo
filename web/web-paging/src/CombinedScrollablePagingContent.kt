package org.solvo.web

import androidx.compose.runtime.*

@Composable
fun <T> rememberExpandablePagingState(
    pageSlice: Int,
    initialList: List<T> = emptyList(),
): ExpandablePagingState<T> = remember { ExpandablePagingStateImpl.create(initialList, pageSlice) }

@Stable
interface ExpandablePagingState<T> : PagingState<T> {
    val isExpanded: State<Boolean>

    fun expand()

    fun collapse()

    fun switchExpanded() {
        if (isExpanded.value) collapse() else expand()
    }
}

internal class ExpandablePagingStateImpl<T> private constructor(initialList: List<T>, pageSlice: Int) :
    ExpandablePagingState<T>,
    PagingStateImpl<T>(initialList, pageSlice) {

    override val isExpanded: MutableState<Boolean> = mutableStateOf(false)
    override val pageSlice: MutableState<Int> = mutableStateOf(pageSlice)
    private var savedSlice: Int = pageSlice

    override fun expand() {
        if (isExpanded.value) return
        isExpanded.value = true
        savedSlice = pageSlice.value
        pageSlice.value = 1
        update()
    }

    override fun collapse() {
        if (!isExpanded.value) return
        isExpanded.value = false
        pageSlice.value = savedSlice
        update()
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