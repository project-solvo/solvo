package org.solvo.web.viewModel

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.*
import org.solvo.model.api.HasCoid
import org.solvo.model.foundation.Uuid


fun <T> LoadingUuidItem(uuid: Uuid, ready: T?): LoadingUuidItem<T> =
    ComputedLoadingUuidItem(uuid, ready)

fun <T> LoadingUuidItem(uuid: Uuid, ready: StateFlow<T?>): LoadingUuidItem<T> =
    StateFlowLoadingUuidItem(uuid, ready)

abstract class LoadingUuidItem<T> : HasCoid {
    abstract val ready: T?

    @Stable
    abstract fun asFlow(): Flow<T>

    abstract override fun toString(): String

    final override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other !is LoadingUuidItem<*>) return false
        return other.coid == this.coid
    }

    override fun hashCode(): Int = coid.hashCode()
}

@Stable
internal class ComputedLoadingUuidItem<T>(
    override val coid: Uuid,
    override val ready: T?
) : LoadingUuidItem<T>() {
    override fun asFlow(): Flow<T> = if (ready == null) emptyFlow() else flowOf(ready)

    override fun toString(): String = "ComputedLoadingUuidItem(coid=$coid, ready=$ready)"
}

internal class StateFlowLoadingUuidItem<T>(
    @Stable
    override val coid: Uuid,
    @Stable
    val flow: StateFlow<T?>,
) : LoadingUuidItem<T>() {
    override val ready: T?
        get() = flow.value

    override fun asFlow(): Flow<T> = flow.filterNotNull()

    override fun toString(): String = "StateFlowLoadingUuidItem(coid=$coid, ready=$ready)"
}

