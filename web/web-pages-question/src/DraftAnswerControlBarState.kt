package org.solvo.web

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


    fun startDraft(kind: DraftKind)

    fun stopDraft()
}


@JsName("createDraftAnswerControlBarState")
fun DraftAnswerControlBarState(): DraftAnswerControlBarState = DraftAnswerControlBarStateImpl()

class DraftAnswerControlBarStateImpl internal constructor() : DraftAnswerControlBarState {
    override val draftKind = MutableStateFlow<DraftKind?>(null)

    override val isEditorVisible = draftKind.map { it != null }
    override val isDraftButtonsVisible = draftKind.map { it == null }
    override val isHideEditorVisible = isEditorVisible.map { !it }

    override fun startDraft(kind: DraftKind) {
        draftKind.value = kind
    }

    override fun stopDraft() {
        this.draftKind.value = null
    }
}
