package org.solvo.web.session

import kotlinx.browser.localStorage
import org.w3c.dom.Storage

class StorageItem internal constructor(
    private val storage: Storage,
    private val name: String,
) {
    var value: String?
        set(value) {
            if (value == null) {
                storage.removeItem(name)
            } else {
                storage.setItem(name, value)
            }
        }
        get() = storage.getItem(name)

    fun remove() {
        value = null
    }
}

fun Storage.item(name: String) = StorageItem(this, name)

val LocalSessionToken = localStorage.item("token")
