package org.solvo.web.pages.article.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.utils.UserPermission
import org.solvo.model.utils.canManageArticle
import org.solvo.web.document.History
import org.solvo.web.event.WithDeletedMessage
import org.solvo.web.pages.article.settings.groups.AddQuestionSettingGroup
import org.solvo.web.pages.article.settings.groups.ArticleSettingGroup
import org.solvo.web.pages.article.settings.groups.QuestionSettingGroup
import org.solvo.web.session.currentUser
import org.solvo.web.session.isLoggedInOrNull
import org.solvo.web.settings.SettingsPage
import org.solvo.web.settings.components.VerticalNavigationList
import org.solvo.web.settings.components.VerticalNavigationListScope
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.CenteredTipText
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon

fun main() {
    onWasmReady {
        SolvoWindow {
            val model: PageViewModel = remember { PageViewModel() }

            SolvoTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val courseCode by model.courseCode.collectAsState(null)
                        val article by model.article.collectAsState(null)
                        CourseArticleCodeLabel(courseCode, article)
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

            val isFullscreen by model.isFullscreen.collectAsState()
            LoadableContent(isLoading = user?.permission != UserPermission.ROOT, Modifier.fillMaxSize()) {
                WithDeletedMessage(model.courseDeleted, "Course") {
                    WithDeletedMessage(model.articleDeleted, "Article") {
                        Row(
                            Modifier.padding(end = 24.dp).fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val modifier = if (isFullscreen) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier.widthIn(min = 600.dp, max = 1000.dp)
                            }

                            Column(modifier) {
                                Page(model)
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun CourseArticleCodeLabel(courseCode: String?, article: ArticleDownstream?) {
    OutlinedCard(shape = RoundedCornerShape(8.dp)) {
        Row(
            Modifier.padding(horizontal = 8.dp).padding(top = 2.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideTextStyle(TextStyle(fontSize = 16.sp)) {
                Text(
                    courseCode ?: "",
                    Modifier.cursorHoverIcon(CursorIcon.POINTER)
                        .clickable(indication = null) {
                            History.navigate {
                                course(
                                    courseCode ?: ""
                                )
                            }
                        }
                )
                Text(" / ")
                Text(article?.code ?: "")
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
            VerticalNavigationList(Modifier.padding(end = 48.dp)) {
                val questionGroups = remember(settingGroups) {
                    settingGroups.orEmpty().filterIsInstance<QuestionSettingGroup>()
                }

                for (entry in ArticleSettingGroup.articleSettingGroups) {
                    Item(selected, entry, model)
                }

                GroupingHeader("Questions")

                for (entry in questionGroups) {
                    Item(selected, entry, model, title = { entry.questionCode })
                }

                if (currentUser?.permission?.canManageArticle() == true) {
                    GroupingHeader("Management")

                    Item(selected, AddQuestionSettingGroup, model, title = { "Add Question" })
                }
            }
        },
        Modifier.verticalScroll(rememberScrollState())
    ) {
        if (selected == null) {
            val name by model.settingGroupName.collectAsState()
            CenteredTipText(remember { derivedStateOf { "$name not found" } }.value)
            CenteredTipText(remember { derivedStateOf { "Please select a question from the list" } }.value)
        } else {
            selected?.run {
                PageContent(model)
            }
        }
    }
}

@Composable
private fun VerticalNavigationListScope.Item(
    selected: ArticleSettingGroup?,
    entry: ArticleSettingGroup,
    model: PageViewModel,
    modifier: Modifier = Modifier,
    title: @Composable () -> String = { entry.pathName.replaceFirstChar { it.titlecaseChar() } },
) {
    Item(
        selected = selected,
        entry = entry,
        onClick = onClick@{
            if (entry == selected) return@onClick
            if (!entry.requestExit()) return@onClick
            History.pushState {
                articleSettings(
                    model.courseCode.value,
                    model.articleCode.value,
                    entry.pathName,
                )
            }
        },
        modifier = modifier,
        title = title,
    )
}

