package org.solvo.web.settings.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AutoCheckPropertyTextField(
    property: AutoCheckProperty<String, String>,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    val value by property.valueFlow.collectAsState()
    val error by property.error.collectAsState()
    val hasError by property.hasError.collectAsState()
    OutlinedTextField(
        value,
        { property.setValue(it) },
        modifier,
        placeholder = placeholder,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        label = label,
        supportingText = {
            if (error != null) {
                Text(error.toString())
            } else {
                supportingText?.invoke()
            }
        },
        trailingIcon = {
            AvailabilityIndicator(hasError?.not())
        },
        isError = hasError == true,
    )
}