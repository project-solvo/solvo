package org.solvo.web.utils

import org.solvo.model.api.HasCoid

inline fun <T> List<T>.replacedOrAppend(
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

inline fun <T> List<T>.replacedOrPrepend(
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
    if (found) {
        return list
    }

    return mutableListOf<T>().apply {
        add(with)
        addAll(this)
    }
}


inline fun <T : HasCoid> List<T>.replacedOrPrepend(
    with: T,
    filter: (T) -> Boolean = { it.coid == with.coid },
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
    if (found) {
        return list
    }

    return mutableListOf<T>().apply {
        add(with)
        addAll(this)
    }
}
