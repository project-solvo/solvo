package org.solvo.web.pages.article.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.utils.UserPermission
import org.solvo.model.utils.canManageArticle
import org.solvo.web.document.History
import org.solvo.web.pages.article.settings.groups.ArticleSettingGroup
import org.solvo.web.pages.article.settings.groups.QuestionSettingGroup
import org.solvo.web.session.currentUser
import org.solvo.web.session.isLoggedInOrNull
import org.solvo.web.settings.SettingsPage
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.modifiers.clickable

fun main() {
    onWasmReady {
        SolvoWindow {
            val model: PageViewModel = remember { PageViewModel() }

            SolvoTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val courseCode by model.courseCode.collectAsState(null)
                        val article by model.article.collectAsState(null)
                        OutlinedCard(shape = RoundedCornerShape(8.dp)) {
                            Row(
                                Modifier.padding(horizontal = 8.dp).padding(top = 2.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProvideTextStyle(TextStyle(fontSize = 16.sp)) {
                                    Text(courseCode ?: "")
                                    Text(" / ")
                                    Text(article?.code ?: "")
                                }
                            }
                        }
                        Text(
                            article?.displayName ?: "", Modifier.padding(start = 12.dp),
                            fontWeight = FontWeight.W600,
                            fontSize = 24.sp
                        )
                    }
                }
            )

            if (isLoggedInOrNull() == false) {
                SideEffect {
                    History.navigate { auth() }
                }
            }

            val user = currentUser
            if (user != null && !user.permission.canManageArticle()) {
                // not OP
                SideEffect {
                    History.navigate { home() }
                }
            }

            LoadableContent(isLoading = user?.permission != UserPermission.ROOT, Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Column(Modifier.widthIn(min = 600.dp, max = 1000.dp)) {
                        Page(model)
                    }
                }
            }
        }
    }
}

@Composable
fun Page(
    model: PageViewModel,
) {
    val settingGroups by model.settingGroups.collectAsState(null)
    val selected by model.selectedSettingGroup.collectAsState(null)
    SettingsPage(
        pageTitle = null,
        navigationRail = {
            Column(
                Modifier
                    .padding(end = 48.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .fillMaxHeight()
            ) {
                val (questionGroups, otherGroups) = remember(settingGroups) {
                    settingGroups.orEmpty().partition { it is QuestionSettingGroup }
                }

                for (entry in otherGroups) {
                    Item(model, selected, entry)
                }

                Text(
                    "Questions",
                    Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                )

                for (entry in questionGroups) {
                    Item(model, selected, entry)
                }
            }
        },
        Modifier.verticalScroll(rememberScrollState())
    ) {
        selected?.run {
            PageContent(model)
        }
    }
}

@Composable
private fun Item(
    model: PageViewModel,
    selected: ArticleSettingGroup?,
    entry: ArticleSettingGroup,
) {
    ListItem(
        leadingContent = {
            entry.NavigationIcon()
        },
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (!entry.requestExit()) return@clickable
                History.pushState {
                    articleSettings(
                        model.courseCode.value,
                        model.articleCode.value,
                        entry.pathName,
                    )
                }
            }.width(200.dp),
        tonalElevation = if (selected == entry) 2.dp else 0.dp,
        headlineText = { Text(entry.pathName, overflow = TextOverflow.Ellipsis) },
    )
}
