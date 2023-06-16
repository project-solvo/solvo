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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import org.solvo.model.api.communication.User

@Composable
fun RoundedUserAvatar(
    user: User?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    RoundedUserAvatar(user?.avatarUrl, user?.username?.str, size, modifier)
}

@Composable
fun RoundedUserAvatar(
    avatarUrl: String?,
    username: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    AvatarBox(modifier.size(size)) {
        UserAvatarImage(avatarUrl, username, Modifier.matchParentSize())
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
    username: String?,
    modifier: Modifier = Modifier,
) {
    val url = remember(avatarUrl, username) {
        avatarUrl?.takeIf { it.isNotEmpty() } ?: username?.let {
            getFallbackAvatar(username)
        }
    }
    Image(
        rememberImagePainter(
            url,
            default = Icons.Filled.Person,
            error = Icons.Filled.Person,
        ),
        "User Avatar",
        colorFilter = if (url == null) ColorFilter.tint(LocalContentColor.current) else null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
}

@Stable
private fun getFallbackAvatar(username: String) =
    "https://ui-avatars.com/api/?length=2&background=random&name=" + username.replace(' ', '+')

