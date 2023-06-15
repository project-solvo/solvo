package org.solvo.web.answer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.web.ui.foundation.OutlinedTextField
import org.solvo.web.ui.modifiers.clickable

@Composable
fun AddYourCommentFakeTextField(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    @Suppress("NAME_SHADOWING")
    val onClick by rememberUpdatedState(onClick)

    OutlinedTextField(
        "", {},
        modifier
            .height(48.dp)
            .clickable(indication = null) { onClick?.invoke() }
            .padding(top = 6.dp, bottom = 6.dp) // inner
            .fillMaxWidth(),
        readOnly = true,
        placeholder = {
            Text(
                "Add your comment...",
                Modifier.clickable(indication = null) { onClick?.invoke() }
                    .fillMaxWidth()
            )
        },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
        )
    )
}
