package org.solvo.web

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.solvo.model.annotations.Stable
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.AdminSettings
import org.solvo.model.api.communication.User
import org.solvo.model.foundation.Uuid
import org.solvo.model.utils.UserPermission
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.settingGroup
import org.solvo.web.requests.client
import org.solvo.web.utils.replacedOrPrepend
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground

@Stable
interface AdminSettingsPageViewModel {
    val pathParameters: PathParameters

    val settingGroup: SharedFlow<AdminSettingGroup>
    val settings: StateFlow<AdminSettings?>
}

@JsName("createAdminSettingsPageViewModel")
fun AdminSettingsPageViewModel(): AdminSettingsPageViewModel = AdminSettingsPageViewModelImpl()

@Stable
private class AdminSettingsPageViewModelImpl : AbstractViewModel(), AdminSettingsPageViewModel {
    override val pathParameters: PathParameters = PathParameters(WebPagePathPatterns.settingsAdmin)
    override val settingGroup = pathParameters.settingGroup<AdminSettingGroup>().filterNotNull().shareInBackground()

    override val settings: MutableStateFlow<AdminSettings?> = MutableStateFlow(null)


    override fun init() {
        launchInBackground {
            settings.value = client.settings.getSettings()
        }
    }
}

@Stable
interface OperatorsViewModel : AdminSettingsPageViewModel {
    val operators: SharedFlow<List<User>>

    val searchQuery: StateFlow<String?>
    val searchResult: StateFlow<List<UserWithNewPermission>?> // including self
    fun setSearchQuery(query: String?)

    fun setOperator(userId: Uuid)
    fun removeOperator(userId: Uuid)
}

fun OperatorsViewModel.setOperator(userId: Uuid, isOperator: Boolean) {
    if (isOperator) {
        this.setOperator(userId)
    } else {
        this.removeOperator(userId)
    }
}


@JsName("createOperatorsViewModel")
fun OperatorsViewModel(parent: AdminSettingsPageViewModel): OperatorsViewModel = OperatorsViewModelImpl(parent)

@Stable
private class OperatorsViewModelImpl(
    private val parent: AdminSettingsPageViewModel
) : AbstractViewModel(), OperatorsViewModel, AdminSettingsPageViewModel by parent {

    private val localOperators = MutableStateFlow<List<User>>(emptyList())
    private val remoteOperators: SharedFlow<List<User>> =
        settings.filterNotNull().map { it.operators }.shareInBackground(started = SharingStarted.Lazily)

    override val operators: StateFlow<List<User>> =
        merge(remoteOperators, localOperators).stateInBackground(listOf(), started = SharingStarted.Lazily)

    override val searchQuery: MutableStateFlow<String?> = MutableStateFlow(null)

    private val localSearchResult: MutableSharedFlow<List<UserWithNewPermission>?> =
        MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val remoteSearchResult: SharedFlow<List<UserWithNewPermission>?> = searchQuery.filterNotNull()
        .debounce(1000)
        .mapLatest { query ->
            client.accounts.searchUsers(query)
                .map { user ->
                    UserWithNewPermission(user, mutableStateOf(user.permission == UserPermission.OPERATOR))
                }
        }.shareInBackground()

    override val searchResult: StateFlow<List<UserWithNewPermission>?> =
        merge(remoteSearchResult, localSearchResult).stateInBackground()

    override fun setSearchQuery(query: String?) {
        if (query.isNullOrBlank()) {
            searchQuery.value = null
        } else {
            searchQuery.value = query.trim()
        }
        localSearchResult.tryEmit(null)  // explicitly clear results, so that the View shows a progress indicator
    }

    override fun setOperator(userId: Uuid) { // must in search
        val target = searchResult.value?.find { it.user.id == userId } ?: return
        if (target.isOperator.value) return

        target.isOperator.value = true
        launchInBackground {
            client.settings.setOperator(userId)
        }

        localOperators.value = operators.value.replacedOrPrepend(
            { it.id == userId },
            target.user.copy(permission = UserPermission.OPERATOR)
        )
    }

    override fun removeOperator(userId: Uuid) {
        // update search
        searchResult.value?.find { it.user.id == userId }?.let {
            it.isOperator.value = false
        }

        launchInBackground {
            client.settings.removeOperator(userId)
        }

        localOperators.value = operators.value.filterNot { it.id == userId }
    }
}

@Stable
data class UserWithNewPermission(
    val user: User,
    val isOperator: MutableState<Boolean>
)