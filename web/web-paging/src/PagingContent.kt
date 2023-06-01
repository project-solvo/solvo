package org.solvo.web

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> rememberPagingState(pageSlice: Int, initialList: List<T> = emptyList()): PagingState<T> {
    return remember { PagingStateImpl.create(initialList, pageSlice) }
}

interface PagingContentContext<T> {
    val visibleIndices: IntRange
    val visibleItems: List<T>

    @Stable
    val scrollState: ScrollState
}

@Composable
fun <T, S : PagingState<T>> PagingContent(
    state: S,
    controlBar: @Composable (state: S) -> Unit = { PagingControlBar(it) },
    contents: @Composable PagingContentContext<T>.() -> Unit,
) {
    Column {
        controlBar(state)
        contents(state.pagingContext)
    }

}

@Composable
fun <T> PagingControlBar(
    state: PagingState<T>,
    showPagingController: Boolean = true,
    content: @Composable context(ControlBarScope) BoxScope.() -> Unit = {},
) {
    ControlBar {
        Box(Modifier.fillMaxWidth()) {
            if (showPagingController) {
                Row(
                    Modifier.fillMaxHeight().align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            state.clickPrePage()
                        },
                        enabled = state.allowNavigatePrev.value,
                        contentPadding = buttonContentPaddings
                    ) {
                        Icon(Icons.Default.West, "Previous")
                        Text("Previous", Modifier.padding(horizontal = 4.dp))
                    }

                    Text(
                        "${state.currentPage.value + 1} / ${state.pageCount.value}",
                        Modifier.padding(horizontal = 16.dp),
                        fontFamily = FontFamily.Monospace
                    )

                    TextButton(
                        onClick = {
                            state.clickNextPage()
                        },
                        enabled = state.allowNavigateNext.value,
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


@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    elevation: Dp = 1.dp,
    content: @Composable context(ControlBarScope) RowScope.() -> Unit,
) {
    Surface(
        modifier,
        tonalElevation = elevation
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp).height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            content(ControlBarScope, this)
        }
    }
}


@Immutable
object ControlBarScope {
    val buttonContentPaddings = PaddingValues(
        start = 12.dp,
        top = 3.dp,
        end = 12.dp,
        bottom = 3.dp
    )
}