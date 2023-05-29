import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.West
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PagingContent(
    state: PagingViewModel,
    list: List<Any>,
) {
    ControlBar(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxHeight().align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        state.clickPrePage(list)
                    },
                    contentPadding = buttonContentPaddings
                ) {
                    Icon(Icons.Default.West, "Previous")
                    Text("Previous", Modifier.padding(horizontal = 4.dp))
                }

                val currentPage = state.currentPage.value + 1
                val maxPage = state.determineMaxPage() + 1
                Text(
                    "$currentPage / $maxPage",
                    Modifier.padding(horizontal = 16.dp),
                    fontFamily = FontFamily.Monospace
                )

                TextButton(
                    onClick = {
                        state.clickNextPage(list)
                    },
                    contentPadding = buttonContentPaddings
                ) {
                    Text("Next", Modifier.padding(horizontal = 4.dp))
                    Icon(Icons.Default.East, "Next")
                }
            }

            Box(Modifier.align(Alignment.CenterEnd)) {
                FilledTonalButton(
                    {},
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = buttonContentPaddings
                ) {
                    Icon(Icons.Outlined.PostAdd, "Draft Answer", Modifier.fillMaxHeight())

                    Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Text(
                            "Draft Answer",
                            Modifier.padding(start = 4.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    content: @Composable context(ControlBarScope) RowScope.() -> Unit,
) {
//    val shape = RoundedCornerShape(12.dp)
    Surface(
        Modifier,
//            .padding(12.dp)
//            .clip(shape)
//            .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
//            .border(color = MaterialTheme.colorScheme.outline, width = 1.dp, shape = shape)
        tonalElevation = 1.dp
    ) {
        Row(
            modifier.padding(horizontal = 12.dp, vertical = 8.dp).height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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