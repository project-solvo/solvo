package org.solvo.web.session

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.model.User
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground

@Stable
class UserViewModel : AbstractViewModel() {
    /**
     * `null` does not necessarily indicate not logged in
     */
    val user: MutableStateFlow<User?> = MutableStateFlow(null)

    /**
     * `null` means not yet known
     */
    val isLoggedIn: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    override fun init() {
        launchInBackground {
            val self = client.accounts.getSelf()
            user.value = self
            isLoggedIn.value = self != null
        }
    }

    fun logout() {
        LocalSessionToken.remove()
    }
}