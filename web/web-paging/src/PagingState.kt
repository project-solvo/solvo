package org.solvo.web

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*


@Stable
interface PagingState<T> {
    val pageSlice: State<Int>

    val items: List<T>
    val pageCount: State<Int>
    val currentPage: State<Int>
    val currentContent: State<List<T>>
    val allowNavigateNext: State<Boolean>
    val allowNavigatePrev: State<Boolean>

    val pagingContext: PagingContentContext<T>

    fun findItemPage(item: T): Int?
    fun gotoPage(page: Int)
    fun clickPrePage()
    fun clickNextPage()
    fun addItem(item: T)
    fun setAllItems(items: List<T>)
    fun getPageItems(page: Int): List<T>
}

fun <T> PagingState<T>.findItemPageOf(predicate: (T) -> Boolean): Int? {
    val index = items.indexOfFirst(predicate)
    if (index == -1) return null
    return index / pageSlice.value
}

fun <T> PagingState<T>.gotoItem(item: T): Boolean {
    return findItemPage(item)?.let {
        gotoPage(it)
    } != null
}

fun <T> PagingState<T>.gotoItemOf(predicate: (T) -> Boolean): Boolean {
    return findItemPageOf(predicate)?.let {
        gotoPage(it)
    } != null
}

internal open class PagingStateImpl<T> protected constructor(
    initialList: List<T>,
    pageSlice: Int,
) : PagingState<T> {
    // inputs
    override val items: MutableList<T> = initialList.toMutableList()
    override val pageSlice: State<Int> = mutableStateOf(pageSlice)

    // outputs
    override val pageCount: MutableState<Int> = mutableStateOf(0)
    override val currentPage: MutableState<Int> = mutableStateOf(0)

    val currentIndices: MutableState<IntRange> = mutableStateOf(IntRange.EMPTY)
    override val currentContent: MutableState<List<T>> = mutableStateOf(emptyList())

    override val allowNavigateNext: State<Boolean> = derivedStateOf {
        this.currentPage.value < this.pageCount.value - 1
    }
    override val allowNavigatePrev: State<Boolean> = derivedStateOf {
        this.currentPage.value > 0
    }

    private val _editorEnable: MutableState<Boolean> = mutableStateOf(false)

    override val pagingContext = object : PagingContentContext<T> {
        override val visibleIndices: State<IntRange> get() = currentIndices
        override val visibleItems: State<List<T>> get() = currentContent
        override val scrollState: ScrollState = ScrollState(0)
    }

    override fun findItemPage(item: T): Int? {
        val index = items.indexOf(item)
        if (index == -1) return null
        return index / pageSlice.value
    }


    private fun calculatePageCount(size: Int): Int {
        val pageSlice = pageSlice.value
        return if (size % pageSlice == 0) {
            size / pageSlice
        } else {
            size / pageSlice + 1
        }
    }


    override fun gotoPage(page: Int) {
        this.currentPage.value = page.coerceAtMost(this.pageCount.value - 1)
        update()
    }

    override fun clickPrePage() {
        if (allowNavigatePrev.value) {
            this.currentPage.value = this.currentPage.value - 1
        }
        update()
    }

    override fun clickNextPage() {
        if (allowNavigateNext.value) {
            this.currentPage.value = this.currentPage.value + 1
        }
        update()
    }

    override fun getPageItems(page: Int): List<T> {
        if (items.isEmpty()) return emptyList()
        val pageSlice = pageSlice.value
        return items.subList(page * pageSlice, (page * pageSlice + pageSlice).coerceAtMost(items.size))
    }

    protected open fun update() {
        this.pageCount.value = calculatePageCount(items.size)
        val page = this.currentPage.value.coerceIn(0, (this.pageCount.value - 1).coerceAtLeast(0))
        this.currentPage.value = page
        this.currentContent.value = getPageItems(this.currentPage.value)

        val pageSlice = pageSlice.value
        this.currentIndices.value = (page * pageSlice)..<((page * pageSlice + pageSlice).coerceAtMost(items.size))
    }

    override fun addItem(item: T) {
        items.add(item)
        update()
    }

    override fun setAllItems(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        update()
    }

    companion object {
        fun <T> create(
            initialList: List<T>,
            pageSlice: Int,
        ): PagingStateImpl<T> {
            return PagingStateImpl(initialList, pageSlice).apply { update() }
        }
    }
}