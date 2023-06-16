package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.utils.UserPermission
import org.solvo.web.document.History
import org.solvo.web.session.currentUser
import org.solvo.web.session.isLoggedInOrNull
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
    Text("Administration Settings", style = MaterialTheme.typography.headlineLarge)

    Spacer(Modifier.height(40.dp))

    Row {
        val selectedSettingGroup by model.settingGroup.collectAsState(null)

        NavigationRail(Modifier.fillMaxHeight()) {
            for (settingGroup in AdminSettingGroup.entries) {
                NavigationRailItem(
                    selected = selectedSettingGroup == settingGroup,
                    icon = {
                        Icon(settingGroup.icon, null)
                    },
                    onClick = { History.pushState { settingsAdmin(settingGroup.pathName) } },
                    modifier = Modifier.widthIn(min = 100.dp),
                    label = { Text(settingGroup.displayName) },
                    alwaysShowLabel = true,
                )
            }
        }

        Column(Modifier.fillMaxSize()) {
            selectedSettingGroup?.content?.run {
                Header(model)
                PageContent(model)
            }
        }
    }
}

