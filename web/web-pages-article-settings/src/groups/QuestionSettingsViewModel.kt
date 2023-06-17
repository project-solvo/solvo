package org.solvo.web.pages.article.settings.groups

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.solvo.model.annotations.Stable
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.communication.QuestionEditRequest
import org.solvo.model.api.communication.isEmpty
import org.solvo.model.utils.nonBlankOrNull
import org.solvo.web.document.History
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.requests.client
import org.solvo.web.settings.components.AutoCheckProperty
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground


@Stable
interface QuestionSettingsViewModel : PageViewModel {
    override val question: StateFlow<QuestionDownstream?>

    val newCode: AutoCheckProperty<String, String>

//    val content: StateFlow<String>

    fun openQuestionPage() {
        History.navigate {
            question(
                courseCode.value,
                articleCode.value,
                settingGroupName.value ?: return
            )
        }
    }

    fun navigateSettingGroup(path: String) {
        History.pushState {
            articleSettings(courseCode.value, articleCode.value, path)
        }
    }


    fun submitBasicChanges(snackbar: SolvoSnackbar)
    fun submitContentChanges(
        snackbar: SolvoSnackbar,
        editorContent: String?
    )
}


@JsName("createQuestionSettingsViewModel")
fun QuestionSettingsViewModel(
    page: PageViewModel,
    originalQuestion: StateFlow<QuestionDownstream?>,
): QuestionSettingsViewModel = QuestionSettingsSettingsViewModelImpl(
    page,
    originalQuestion,
)

@Stable
class QuestionSettingsSettingsViewModelImpl(
    private val page: PageViewModel,
    originalQuestion: StateFlow<QuestionDownstream?>,
) : QuestionSettingsViewModel, PageViewModel by page, AbstractViewModel() {
    override val question: StateFlow<QuestionDownstream?> = originalQuestion

    override val newCode: AutoCheckProperty<String, String> = AutoCheckProperty(
        originalQuestion.filterNotNull().map { it.code }.stateInBackground(""),
        transformValue = { it.trim() }
    ) { code ->
        when {
            code == originalQuestion.value?.code -> null
            client.questions.isQuestionExist(
                courseCode.value,
                articleCode.value,
                code
            ) -> "Question code '$code' is already taken"

            else -> null
        }
    }

    override fun submitBasicChanges(snackbar: SolvoSnackbar) {
        launchInBackground {
            submitChange(
                snackbar,
                QuestionEditRequest(
                    code = newCode.valueFlow.value.nonBlankOrNull,
                )
            )
        }
    }

    private suspend fun submitChange(snackbar: SolvoSnackbar, req: QuestionEditRequest) {
        if (req.isEmpty()) {
            snackbar.showSnackbar("Already up-to-date")
            return
        }
        var targetCode = question.value?.code
        if (targetCode == null) {
            client.questions.addQuestion(courseCode.value, articleCode.value, newCode.value)
            targetCode = newCode.value
        }

        client.questions.updateQuestion(courseCode.value, articleCode.value, targetCode, req)
        snackbar.showSnackbar("Changes saved")
    }

    override fun init() {
        launchInBackground {
            question.filterNotNull().collect {
                newCode.setValue(it.code)
            }
        }
    }

    override fun submitContentChanges(snackbar: SolvoSnackbar, editorContent: String?) {
        launchInBackground {
            val request = QuestionEditRequest(
                content = editorContent?.nonBlankOrNull?.takeIf { it.str != question.value?.content },
            )
            if (!request.isEmpty()) {
                submitChange(
                    snackbar,
                    request,
                )
            }
        }
    }
}