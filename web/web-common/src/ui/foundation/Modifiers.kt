package org.solvo.web.ui.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
inline fun Modifier.ifThen(condition: Boolean, action: Modifier.() -> Modifier): Modifier {
    return if (condition) action()
    else this
}

@Composable
inline fun Modifier.ifThenElse(
    condition: Boolean,
    then: Modifier.() -> Modifier,
    `else`: Modifier.() -> Modifier
): Modifier {
    return if (condition) then()
    else `else`()
}
