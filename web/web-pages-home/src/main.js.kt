package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.web.document.History
import org.solvo.web.settings.SettingGroup
import org.solvo.web.settings.SettingsPage
import org.solvo.web.settings.components.VerticalNavigationList
import org.solvo.web.settings.components.VerticalNavigationListScope
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()

            val model = remember { HomePageViewModel() }
            val courses by model.courses.collectAsState()

            LoadableContent(isLoading = courses == null, Modifier.fillMaxSize()) {
                Row(Modifier.padding(all = 24.dp).fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Column(Modifier.widthIn(min = 600.dp, max = 1000.dp)) {
                        HomePageContent(
                            model
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageContent(
    model: HomePageViewModel,
) {
    val selected by model.selectedSettingGroup.collectAsState(null)
    val settingGroup by model.settingGroups.collectAsState(null)
    SettingsPage(
        pageTitle = { Text("My Courses") },
        navigationRail = {
            VerticalNavigationList(Modifier.padding(end = 40.dp)) {

                GroupingHeader("Courses")

                settingGroup?.filterIsInstance<CourseSettingGroup>()?.forEach {
                    Item(selected = selected, entry = it, courseCode = it.pathName, title = { it.name.str })
                }

                GroupingHeader("Management")

                settingGroup?.filterIsInstance<AddCourseGroup>()?.forEach {
                    ItemAdd(selected = selected, it, title = { "Add new course" })
                }
            }
        },
        Modifier.verticalScroll(rememberScrollState())
    ) {
        selected?.run {
            PageContent(remember(model) { PageViewModel(model) })
        }
    }

}

@Composable
private fun VerticalNavigationListScope.ItemAdd(
    selected: SettingGroup<PageViewModel>?,
    entry: SettingGroup<PageViewModel>,
    modifier: Modifier = Modifier,
    title: @Composable () -> String = { entry.pathName.replaceFirstChar { it.titlecaseChar() } },
) {
    Item(
        selected = selected,
        entry = entry,
        onClick = onClick@{
            if (entry == selected) return@onClick
            if (!entry.requestExit()) return@onClick
            History.pushState { addCourse() }
        },
        modifier = modifier,
        title = title,
    )
}

@Composable
private fun VerticalNavigationListScope.Item(
    selected: SettingGroup<PageViewModel>?,
    entry: SettingGroup<PageViewModel>,
    courseCode: String,
    modifier: Modifier = Modifier,
    title: @Composable () -> String = { entry.pathName.replaceFirstChar { it.titlecaseChar() } },
) {
    Item(
        selected = selected,
        entry = entry,
        onClick = onClick@{
            if (entry == selected) return@onClick
            if (!entry.requestExit()) return@onClick
            History.pushState { course(courseCode) }
        },
        modifier = modifier,
        title = title,
    )
}



