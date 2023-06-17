package org.solvo.web.pages.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.utils.UserPermission
import org.solvo.web.document.History
import org.solvo.web.pages.admin.groups.AdminSettingGroup
import org.solvo.web.session.currentUser
import org.solvo.web.session.isLoggedInOrNull
import org.solvo.web.settings.SettingsNavigationRail
import org.solvo.web.settings.SettingsPage
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            if (isLoggedInOrNull() == false) {
                SideEffect {
                    History.navigate { auth() }
                }
            }

            val user = currentUser
            if (user != null && user.permission != UserPermission.ROOT) {
                // not ROOT
                SideEffect {
                    History.navigate { home() }
                }
            }

            val model: AdminSettingsPageViewModel = remember { AdminSettingsPageViewModel() }

            LoadableContent(isLoading = user?.permission != UserPermission.ROOT, Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Column(Modifier.widthIn(min = 600.dp, max = 1000.dp)) {
                        AdminPage(model)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPage(
    model: AdminSettingsPageViewModel,
) {
    val selectedSettingGroup by model.settingGroup.collectAsState(null)
    SettingsPage(
        pageTitle = { Text("Administration Settings") },
        navigationRail = {
            SettingsNavigationRail(
                AdminSettingGroup.entries,
                selectedSettingGroup,
                onClick = {
                    History.pushState { settingsAdmin(it.pathName) }
                }
            )
        }
    ) {
        selectedSettingGroup?.run {
            PageContent(model)
        }
    }
}
