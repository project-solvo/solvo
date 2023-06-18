package org.solvo.web.settings.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.web.settings.SettingGroup
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.modifiers.clickable

@Composable
fun VerticalNavigationList(
    modifier: Modifier = Modifier,
    content: @Composable VerticalNavigationListScope.() -> Unit
) {
    Column(
        modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        val scope = remember(this) { VerticalNavigationListScope(this) }
        content(scope)
    }
}

@LayoutScopeMarker
@Immutable
class VerticalNavigationListScope(
    private val delegate: ColumnScope
) : ColumnScope by delegate {

    @Composable
    fun GroupingHeader(text: String) {
        Text(
            text,
            Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        )
    }

    @Composable
    fun <T : SettingGroup<*>> Item(
        selected: T?,
        entry: T,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        title: @Composable () -> String = { entry.pathName.replaceFirstChar { it.titlecaseChar() } },
    ) {
        Item(
            title(),
            isSelected = selected == entry,
            onClick = onClick,
            modifier = modifier,
        ) {
            entry.NavigationIcon()
        }
    }

    @Composable
    fun Item(
        title: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable (() -> Unit)? = null,
    ) {
        val interactions = remember { MutableInteractionSource() }
        val isHovered by interactions.collectIsHoveredAsState()

        ListItem(
            leadingContent = icon,
            modifier = modifier
                .padding(vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .hoverable(interactions)
                .clickable(indication = rememberRipple(), onClick = wrapClearFocus(onClick))
                .width(200.dp),
            tonalElevation = if (isSelected || isHovered) 2.dp else 0.dp,
            headlineText = {
                Text(
                    title,
                    overflow = TextOverflow.Ellipsis
                )
            },
        )
    }
}