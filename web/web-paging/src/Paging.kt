package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.solvo.web.ControlBarScope.buttonContentPaddings

@Composable
fun <T> rememberPagingState(initialList: List<T>, pageSlice: Int): PagingState<T> {
    return remember { PagingStateImpl(initialList, pageSlice) }
}

@Composable
fun <T> PagingContent(
    state: PagingState<T>,
    controlBar: @Composable (state: PagingState<T>) -> Unit = { PagingControlBar(it) },
    contents: @Composable (List<T>) -> Unit,
) {
    Column {
        controlBar(state)
        contents(state.items)
    }

}

@Composable
fun <T> PagingControlBar(
    state: PagingState<T>,
    content: @Composable context(ControlBarScope) RowScope.() -> Unit = {},
) {
    Surface(
        Modifier,
        tonalElevation = 1.dp
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp).height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxHeight().align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            state.clickPrePage()
                        },
                        enabled = state.prevButton(),
                        contentPadding = buttonContentPaddings
                    ) {
                        Icon(Icons.Default.West, "Previous")
                        Text("Previous", Modifier.padding(horizontal = 4.dp))
                    }

                    Text(
                        "${state.currentPage.value + 1} / ${state.pageCount.value + 1}",
                        Modifier.padding(horizontal = 16.dp),
                        fontFamily = FontFamily.Monospace
                    )

                    TextButton(
                        onClick = {
                            state.clickNextPage()
                        },
                        enabled = state.nextButton(),
                        contentPadding = buttonContentPaddings
                    ) {
                        Text("Next", Modifier.padding(horizontal = 4.dp))
                        Icon(Icons.Default.East, "Next")
                    }
                }
            }

            content(ControlBarScope, this)
        }
    }
}


object ControlBarScope {
    val buttonContentPaddings = PaddingValues(
        start = 12.dp,
        top = 3.dp,
        end = 12.dp,
        bottom = 3.dp
    )
}