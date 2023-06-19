package org.solvo.web.comments.commentCard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.web.ui.theme.UNICODE_FONT


@Composable
fun AuthorLine(
    icon: @Composable BoxScope.() -> Unit,
    authorName: @Composable RowScope.() -> Unit,
    date: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    Row(modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            icon.invoke(this)
        }

        Spacer(Modifier.width(12.dp)) // 8.dp
        Column(Modifier.weight(1f)) {
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(AuthorNameTextStyle) {
                    authorName()
                }
            }
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(AuthorLineDateTextStyle) {
                    date()
                }
            }
        }

        Row(
            Modifier.wrapContentWidth().height(48.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End
        ) {
            actions()
        }
    }
}

@Stable
val AuthorNameTextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    fontFamily = UNICODE_FONT,
    textAlign = TextAlign.Center,
    lineHeight = 22.sp,
)

@Stable
val AuthorLineDateTextStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    fontFamily = UNICODE_FONT,
    textAlign = TextAlign.Center,
    lineHeight = 18.sp,
)


@Composable
fun AuthorLineThin(
    icon: @Composable BoxScope.() -> Unit,
    authorName: String,
    date: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier.height(25.dp)) {
        Box(Modifier.size(30.dp)) {
            icon.invoke(this)
        }
        Box {
            ProvideTextStyle(AuthorNameTextStyle) {
                Text(authorName, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 3.dp))
            }
        }
//        Box(modifier = Modifier.offset(y = (-2).dp)) {
//            Text("(last edited: $date)", fontSize = 15.sp)
//        }
    }
}
