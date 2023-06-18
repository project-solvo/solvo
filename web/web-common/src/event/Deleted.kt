package org.solvo.web.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.web.ui.foundation.CenteredTipText

enum class DeletedKind {
    DELETED,
    NOT_FOUND,
}

interface Deleted {
    @Stable
    val isDeleted: StateFlow<Boolean>

    @Stable
    val isNotFound: StateFlow<Boolean>

    @Stable
    val error: Flow<DeletedKind?>

    fun setDeleted()
    fun setNotFound()

    companion object {
        fun create(): Deleted = DeletedImpl()
        fun ignored(): Deleted = IgnoredDeleted
    }
}

inline fun <R> Deleted.wrapNotFound(action: () -> R): R {
    return try {
        action()
    } catch (e: Throwable) {
        setNotFound()
        throw e
    }
}

@JsName("createDeleted")
fun Deleted(): Deleted = Deleted.create()

@Composable
fun DeletedMessage(
    deleted: Deleted,
    name: String
): Boolean {
    val error by deleted.error.collectAsState(null)

    val text = remember(name) {
        when (error) {
            null -> null
            DeletedKind.DELETED -> "$name had been deleted"
            DeletedKind.NOT_FOUND -> "$name was not found"
        }
    } ?: return false

    CenteredTipText(text)
    return true
}

@Composable
fun WithDeletedMessage(
    deleted: Deleted,
    name: String,
    content: @Composable () -> Unit
) {
    val error by deleted.error.collectAsState(null)
    if (error == null) {
        content()
    } else {
        DeletedMessage(deleted, name)
    }
}

private object IgnoredDeleted : Deleted {
    override val isDeleted: StateFlow<Boolean> = MutableStateFlow(false)
    override val isNotFound: StateFlow<Boolean> = MutableStateFlow(false)
    override val error: Flow<DeletedKind?> = flowOf()

    override fun setDeleted() {
    }

    override fun setNotFound() {
    }
}

private class DeletedImpl : Deleted {
    override val isDeleted = MutableStateFlow(false)
    override val isNotFound: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val error: Flow<DeletedKind?> = combine(isDeleted, isNotFound) { isDeleted, isNotFound ->
        when {
            isDeleted -> DeletedKind.DELETED
            isNotFound -> DeletedKind.NOT_FOUND
            else -> null
        }
    }

    override fun setDeleted() {
        this.isDeleted.value = true
    }

    override fun setNotFound() {
        this.isNotFound.value = true
    }
}
