package org.solvo.web.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

@Composable
fun RoundedUserAvatar(
    avatarUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    AvatarBox(modifier.size(size)) {
        UserAvatarImage(avatarUrl, Modifier.matchParentSize())
    }
}


@Composable
fun AvatarBox(
    modifier: Modifier = Modifier,
    image: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(color = Color.Gray)
//            .border(color = MaterialTheme.colorScheme.outline, width = 1.dp, shape = CircleShape)
    ) {
        image()
    }
}

@Composable
fun UserAvatarImage(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    Image(
        rememberImagePainter(
            avatarUrl?.takeIf { it.isNotEmpty() },
            default = Icons.Filled.Person,
            error = Icons.Filled.Person,
        ),
        "User Avatar",
        colorFilter = if (avatarUrl == null) ColorFilter.tint(LocalContentColor.current) else null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
}

