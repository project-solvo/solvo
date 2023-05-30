package org.solvo.web

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList


interface PagingState<T> {
    val items: MutableList<T>
    val pageCount: State<Int>
    val currentPage: State<Int>
    val currentContent: State<List<T>>
    fun gotoPage(page: Int)
    fun clickPrePage()
    fun clickNextPage()
    fun addItem(item: T)
    fun nextButton(): Boolean
    fun prevButton(): Boolean
    fun getPageItems(page: Int): List<T>
}

@Stable
internal class PagingStateImpl<T>(
    private val initialList: List<T>,
    private val pageSlice: Int,
) : PagingState<T> {
    override val items: MutableList<T> = SnapshotStateList<T>().apply { addAll(initialList) }

    private val _pageCount: MutableState<Int> = mutableStateOf(getPageNumber(items.size))

    private fun getPageNumber(size: Int): Int {
        return if (size % pageSlice == 0) {
            size / pageSlice
        } else {
            size / pageSlice + 1
        }
    }

    private val _currentPage: MutableState<Int> = mutableStateOf(0)


    private val _currentContent: MutableState<List<T>> = mutableStateOf(getPageItems(0))
    override val pageCount: State<Int>
        get() = _pageCount
    override val currentPage: State<Int>
        get() = _currentPage
    override val currentContent: State<List<T>>
        get() = _currentContent

    override fun gotoPage(page: Int) {
        _currentPage.value = page.coerceAtMost(_pageCount.value - 1)
        _currentContent.value = getPageItems(_currentPage.value)
    }

    override fun clickPrePage() {
        if (prevButton()) {
            _currentPage.value = _currentPage.value - 1
        }
        _currentContent.value = getPageItems(_currentPage.value)
    }

    override fun clickNextPage() {
        if (nextButton()) {
            _currentPage.value = _currentPage.value + 1
        }
        _currentContent.value = getPageItems(_currentPage.value)
    }

    override fun getPageItems(page: Int): List<T> {
        val contents = mutableListOf<T>()
        return if (items.size == 0) {
            contents
        } else {
            for (i in page * pageSlice until page * pageSlice + pageSlice) {
                if (i < items.size) {
                    contents.add(items[i])
                }
            }
            contents
        }

    }

    override fun prevButton(): Boolean {
        return _currentPage.value > 0
    }

    override fun nextButton(): Boolean {
        return _currentPage.value < pageCount.value - 1
    }

    override fun addItem(item: T) {
        items.add(item)
    }

}
