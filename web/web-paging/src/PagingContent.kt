package org.solvo.web

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> rememberPagingState(pageSlice: Int, initialList: List<T> = emptyList()): PagingState<T> {
    return remember { PagingStateImpl.create(initialList, pageSlice) }
}

@Stable
interface PagingContentContext<T> {
    val visibleIndices: State<IntRange>
    val visibleItems: State<List<T>>

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
    showPagingController: Boolean = false,
    content: @Composable context(ControlBarScope) BoxScope.() -> Unit = {},
) {
    ControlBar {
        Box(Modifier.fillMaxWidth()) {
//            if (showPagingController) {
//                Row(
//                    Modifier.fillMaxHeight().align(Alignment.Center),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    TextButton(
//                        onClick = {
//                            state.clickPrePage()
//                        },
//                        enabled = state.allowNavigatePrev.value,
//                        contentPadding = buttonContentPaddings
//                    ) {
//                        Icon(Icons.Default.West, "Previous")
//                        Text("Previous", Modifier.padding(horizontal = 4.dp))
//                    }
//
//                    Text(
//                        "${state.currentPage.value + 1} / ${state.pageCount.value}",
//                        Modifier.padding(horizontal = 16.dp),
//                        fontFamily = FontFamily.Monospace
//                    )
//
//                    TextButton(
//                        onClick = {
//                            state.clickNextPage()
//                        },
//                        enabled = state.allowNavigateNext.value,
//                        contentPadding = buttonContentPaddings
//                    ) {
//                        Text("Next", Modifier.padding(horizontal = 4.dp))
//                        Icon(Icons.Default.East, "Next")
//                    }
//                }
//            }

            ProvideTextStyle(textStyle) {
                content(ControlBarScope, this)
            }
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

    val buttonSpacing = 12.dp

    val buttonShape = RoundedCornerShape(8.dp)

    val textStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        fontWeight = FontWeight.W500,
    )
}