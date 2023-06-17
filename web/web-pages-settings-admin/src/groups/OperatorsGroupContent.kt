package org.solvo.web.pages.admin.groups

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.model.api.communication.User
import org.solvo.web.pages.admin.AdminSettingsPageViewModel
import org.solvo.web.pages.admin.OperatorsViewModel
import org.solvo.web.pages.admin.UserWithNewPermission
import org.solvo.web.pages.admin.setOperator
import org.solvo.web.session.currentUser
import org.solvo.web.settings.CenteredTipText
import org.solvo.web.settings.Section
import org.solvo.web.settings.SimpleHeader
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.foundation.CheckThumbSwitch
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.foundation.wrapClearFocus1
import org.solvo.web.ui.image.RoundedUserAvatar
import org.solvo.web.ui.theme.UNICODE_FONT

data object OperatorsSettingGroup : AdminSettingGroup(
    "operators",
    "Operators"
) {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Outlined.AdminPanelSettings, null)
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: AdminSettingsPageViewModel) {
        SimpleHeader(displayName)
        Text(
            "Operators can manage courses and questions",
            style = TextStyle(
                color = LocalContentColor.current.copy(0.8f),
                fontFamily = UNICODE_FONT,
            )
        )

        val model = remember(viewModel) { OperatorsViewModel(viewModel) }

        val result by model.searchResult.collectAsState()
        Section(
            { Text("Add Operator") },
            Modifier.wrapContentSize()
        ) {
            val searchQuery by model.searchQuery.collectAsState()

            Row {
                OutlinedTextField(
                    searchQuery ?: "",
                    { model.setSearchQuery(it) },
                    Modifier.padding(vertical = 12.dp).wrapContentWidth(),
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    placeholder = { Text("Search users by name") },
                    trailingIcon = {
                        AnimatedVisibility(
                            !searchQuery.isNullOrBlank(),
                            enter = slideInHorizontally { it } + fadeIn(),
                            exit = slideOutHorizontally { it } + fadeOut()
                        ) {
                            Icon(
                                Icons.Outlined.Clear,
                                null,
                                Modifier.clickable(onClick = wrapClearFocus {
                                    model.setSearchQuery(null)
                                })
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions {

                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (searchQuery != null) {
                LoadableContent(
                    result == null,
                    loadingContent = { CircularProgressIndicator(Modifier.padding(vertical = 24.dp)) }
                ) {
                    SearchResult(
                        result ?: return@LoadableContent,
                        model,
                        Modifier.padding(vertical = 16.dp).fillMaxWidth().wrapContentHeight()
                    )
                }
            }
        }

        val operators by model.operators.collectAsState(null)
        Section({ Text("All Operators") }, Modifier.heightIn(min = 600.dp)) {
            Spacer(Modifier.height(12.dp))

            LoadableContent(
                operators == null,
                loadingContent = { CircularProgressIndicator(Modifier.padding(top = 24.dp)) }
            ) {
                OperatorsList(
                    operators ?: return@LoadableContent,
                    model,
                    Modifier.fillMaxSize()
                )
            }
        }
    }

    @Composable
    private fun OperatorsList(
        operators: List<User>,
        model: OperatorsViewModel,
        modifier: Modifier = Modifier,
    ) {
        if (operators.isEmpty()) {
            CenteredTipText("There are no operators")
        } else {
            LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(operators) { user ->
                    UserLine(user) {
                        Button(
                            { model.removeOperator(user.id) },
                            Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        ) {
                            Icon(Icons.Filled.Delete, null)
                            Text("Remove Operator")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchResult(
        result: List<UserWithNewPermission>,
        model: OperatorsViewModel,
        modifier: Modifier = Modifier,
    ) {
        if (result.isEmpty()) {
            CenteredTipText("No users found by given search query")
        } else Column(modifier) {
            Text(
                "Set Operator",
                Modifier.padding(end = 12.dp).align(Alignment.End),
                style = MaterialTheme.typography.titleMedium,
            )

            val currentUser by rememberUpdatedState(currentUser)
            LazyColumn(
                Modifier.padding(top = 8.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(result) { (user, isOperator) ->
                    UserLine(user) {
                        CheckThumbSwitch(
                            isOperator.value || currentUser?.id == user.id,
                            wrapClearFocus1 { model.setOperator(user.id, it) },
                            Modifier.padding(start = 12.dp),
                            enabled = currentUser != null && currentUser?.id != user.id,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun UserLine(user: User, actions: @Composable RowScope.() -> Unit) {
        OutlinedCard(
            Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.3f))
        ) {
            Box(Modifier.padding(all = 16.dp).height(32.dp).fillMaxWidth()) {
                Row(Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
                    RoundedUserAvatar(user, 32.dp)
                    Text(
                        user.username.str,
                        Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
                    )
                }

                Row(Modifier.align(Alignment.CenterEnd)) {
                    actions()
                }
            }
        }
    }

}