package org.solvo.web

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.model.Course


@Composable
fun PaperView(
    questionSelectedBar: @Composable RowScope. () -> Unit,
    onChangeLayout: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        ControlBar(Modifier.fillMaxWidth()) {
            Row(
                Modifier.weight(1f)
            ) {
                questionSelectedBar()
            }

            IconButton(onChangeLayout) {
                if (isExpanded) {
                    Icon(Icons.Default.Compress, "Compress")
                } else {
                    Icon(Icons.Default.Expand, "Expand")
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            content()
        }
    }
}


@Composable
fun PaperTitle(
    course: Course,
    year: String,
) {
    Row {
        Text(course.code, fontWeight = FontWeight.W800, fontSize = 22.sp)
        Text(course.name, Modifier.padding(start = 4.dp), fontWeight = FontWeight.W800, fontSize = 22.sp)
        Text(year, Modifier.padding(start = 16.dp), fontWeight = FontWeight.W700, fontSize = 18.sp)
    }

}
