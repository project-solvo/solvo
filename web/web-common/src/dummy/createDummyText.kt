package org.solvo.web.dummy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlin.random.Random


@Composable
fun rememberDummyText(debugId: Any? = null) =
    remember(debugId) {
        createDummyText(debugId)
    }

@Stable
fun createDummyText(debugId: Any?): String {
    val debugIdText = debugId?.let { "[$it] " }
    return if (Random.nextBoolean()) "${debugIdText}Short message"
    else {
        """${debugIdText}Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc dignissim facilisis dui, vitae suscipit velit molestie in. Sed at finibus sem. Vestibulum nibh nunc, blandit sit amet semper eget, varius at enim. Suspendisse porta blandit est, semper tincidunt nunc porta et. Suspendisse consequat quam eu dui mattis mollis. Donec est orci, luctus sit amet iaculis ut, convallis ac libero. Quisque porttitor commodo lorem ac sagittis. Aliquam lobortis leo nisi, at rhoncus felis molestie viverra. Pellentesque accumsan tincidunt molestie. Vivamus non ligula rhoncus, ultricies libero ac, feugiat nisl. Cras quis convallis nunc. Mauris at est in ante consequat venenatis.""".trimIndent()
    }
}
