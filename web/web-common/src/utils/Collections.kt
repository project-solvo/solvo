package org.solvo.web.utils

inline fun <T> List<T>.replacedOrPlus(
    filter: (T) -> Boolean,
    with: T
): List<T> {
    var found = false
    val list = mutableListOf<T>()
    this.mapTo(list) {
        if (filter(it)) {
            found = true
            with
        } else {
            it
        }
    }
    if (!found) {
        list.add(with)
    }
    return list
}
