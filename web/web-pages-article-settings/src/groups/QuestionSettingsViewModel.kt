package org.solvo.web.pages.article.settings.groups

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import org.solvo.model.annotations.Stable
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.communication.QuestionEditRequest
import org.solvo.model.api.communication.isEmpty
import org.solvo.model.utils.nonBlankOrNull
import org.solvo.web.document.History
import org.solvo.web.document.parameters.question
import org.solvo.web.pages.article.settings.PageViewModel
import org.solvo.web.requests.client
import org.solvo.web.settings.components.AutoCheckProperty
import org.solvo.web.ui.snackBar.SolvoSnackbar
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground


@Stable
interface QuestionSettingsViewModel : PageViewModel {
    val question: StateFlow<QuestionDownstream?>

    val newCode: AutoCheckProperty<String, String>

//    val content: StateFlow<String>

    fun openQuestionPage() {
        History.navigate {
            question(
                courseCode.value,
                articleCode.value,
                questionCode.value ?: return
            )
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
): QuestionSettingsViewModel = QuestionSettingsSettingsViewModelImpl(page)

@Stable
class QuestionSettingsSettingsViewModelImpl(
    private val page: PageViewModel,
) : QuestionSettingsViewModel, PageViewModel by page, AbstractViewModel() {
    override val question: StateFlow<QuestionDownstream?> =
        page.pathParameters.question(WebPagePathPatterns.VAR_SETTING_GROUP).filterNotNull().stateInBackground()

    override val newCode: AutoCheckProperty<String, String> = AutoCheckProperty(
        "",
        transformValue = { it.trim() }
    ) { code ->
        when {
            code == articleCode.value -> null
            !client.questions.isQuestionExist(
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
        questionCode.value?.let {
            client.questions.updateQuestion(
                courseCode.value, articleCode.value, it,
                req
            )
        }
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
            submitChange(
                snackbar,
                QuestionEditRequest(
                    content = editorContent?.nonBlankOrNull,
                ),
            )
        }
    }
}