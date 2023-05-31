package org.solvo.web

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList


@Stable
interface PagingState<T> {
    val pageSlice: State<Int>

    val items: MutableList<T>
    val pageCount: State<Int>
    val currentPage: State<Int>
    val currentContent: State<List<T>>
    val allowNavigateNext: State<Boolean>
    val allowNavigatePrev: State<Boolean>

    fun findItemPage(item: T): Int?
    fun gotoPage(page: Int)
    fun clickPrePage()
    fun clickNextPage()
    fun addItem(item: T)
    fun setAllItems(items: List<T>)
    fun getPageItems(page: Int): List<T>
}

internal open class PagingStateImpl<T> protected constructor(
    initialList: List<T>,
    pageSlice: Int,
) : PagingState<T> {
    // inputs
    override val items: MutableList<T> = SnapshotStateList<T>().apply { addAll(initialList) }
    override val pageSlice: State<Int> = mutableStateOf(pageSlice)

    // outputs
    override val pageCount: MutableState<Int> = mutableStateOf(0)
    override val currentPage: MutableState<Int> = mutableStateOf(0)
    override val currentContent: MutableState<List<T>> = mutableStateOf(emptyList())

    override val allowNavigateNext: State<Boolean> = derivedStateOf {
        this.currentPage.value < this.pageCount.value - 1
    }
    override val allowNavigatePrev: State<Boolean> = derivedStateOf {
        this.currentPage.value > 0
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
        this.currentPage.value = this.currentPage.value.coerceIn(0, (this.pageCount.value - 1).coerceAtLeast(0))
        this.currentContent.value = getPageItems(this.currentPage.value)
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
