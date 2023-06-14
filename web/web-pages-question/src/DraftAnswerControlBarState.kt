package org.solvo.web

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.solvo.model.annotations.Stable

@Stable
interface DraftAnswerControlBarState {
    val draftKind: Flow<DraftKind?>

    val isEditorVisible: Flow<Boolean>
    val isDraftButtonsVisible: Flow<Boolean>
    val isHideEditorVisible: Flow<Boolean>

    val isAnonymousNew: State<Boolean>
    fun setAnonymous(isAnonymous: Boolean)

    fun startDraft(kind: DraftKind) // can start multiple times

    fun stopDraft()
}


@JsName("createDraftAnswerControlBarState")
fun DraftAnswerControlBarState(): DraftAnswerControlBarState = DraftAnswerControlBarStateImpl()

class DraftAnswerControlBarStateImpl internal constructor() : DraftAnswerControlBarState {
    override val draftKind = MutableStateFlow<DraftKind?>(null)

    override val isEditorVisible = draftKind.map { it != null }
    override val isDraftButtonsVisible = draftKind.map { it == null }
    override val isHideEditorVisible = isEditorVisible.map { !it }
    override val isAnonymousNew: MutableState<Boolean> = mutableStateOf(false)

    override fun setAnonymous(isAnonymous: Boolean) {
        isAnonymousNew.value = isAnonymous
    }

    override fun startDraft(kind: DraftKind) {
        draftKind.value = kind
        if (kind is DraftKind.Edit) {
            isAnonymousNew.value = kind.comment.anonymity
        }
    }

    override fun stopDraft() {
        this.draftKind.value = null
    }
}
