package org.solvo.web.pages.article.settings.groups

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.web.pages.article.settings.PageViewModel

data object ArticlePropertiesSettingGroup : ArticleSettingGroup("basics") {
    @Composable
    override fun NavigationIcon() {
        Icon(Icons.Outlined.Article, null)
    }

    @Composable
    override fun ColumnScope.PageContent(viewModel: PageViewModel) {
        val model = remember(viewModel) { ArticlePropertiesViewModel(viewModel) }

        Row {
            val newCode by model.newCode.collectAsState()
            val newCodeError by model.newCodeError.collectAsState()
            OutlinedTextField(
                newCode,
                { model.setNewCode(it) },
                Modifier.fillMaxWidth(),
                placeholder = { Text("Example: 2023") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                label = { Text("Article Code") },
                supportingText = {
                    Text(newCodeError ?: "Each article should have an unique code. ")
                },
                trailingIcon = {
                    val isAvailable by model.isNewCodeAvailable.collectAsState()
                    AvailabilityIndicator(isAvailable)
                },
                isError = newCodeError != null,
            )
        }
    }
}
