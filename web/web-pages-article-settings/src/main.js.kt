package org.solvo.web.pages.article.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.utils.UserPermission
import org.solvo.model.utils.canManageArticle
import org.solvo.web.document.History
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
            SolvoTopAppBar()

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

            val model: PageViewModel = remember { PageViewModel() }

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
    val questionGroups by model.questionGroups.collectAsState(null)
    val selected by model.selectedQuestionGroup.collectAsState(null)
    val article by model.article.collectAsState(null)
    SettingsPage(
        pageTitle = { Text(article?.displayName ?: "") },
        navigationRail = {
            Column(
                Modifier
                    .padding(end = 48.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .fillMaxHeight()
            ) {
                Text(
                    "Questions",
                    Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                )
                for (entry in questionGroups.orEmpty()) {
                    ListItem(
                        leadingContent = {
                            entry.NavigationIcon()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                History.pushState {
                                    articleSettings(
                                        model.courseCode.value,
                                        model.articleCode.value,
                                        entry.questionCode,
                                    )
                                }
                            }.width(200.dp),
                        tonalElevation = if (selected == entry) 2.dp else 0.dp,
                        headlineText = { Text(entry.questionCode, overflow = TextOverflow.Ellipsis) },
                    )
                }
            }
        }
    ) {
        selected?.run {
            PageContent(model)
        }
    }
}
