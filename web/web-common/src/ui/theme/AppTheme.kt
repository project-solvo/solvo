package org.solvo.web.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import org.jetbrains.compose.resources.resource
import org.solvo.web.ui.isBrowserInDarkTheme


var UNICODE_FONT by mutableStateOf<FontFamily?>(null)
var EMOJI_FONT by mutableStateOf<FontFamily?>(null)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isBrowserInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    LaunchedEffect(true) {
        try {
            UNICODE_FONT = FontFamily(
//                Typeface(org.jetbrains.skia.Typeface.makeFromName("FangZhengHeiTiSC", FontStyle.NORMAL)),
                Font("FangZhengHeiTiSC", resource("fonts/FangZhengHeiTiSC.ttf").readBytes()),
//            Font("FangZhengHeiTiTC", resource("fonts/FangZhengHeiTiTC.ttf").readBytes()),
            )
        } catch (e: Throwable) {
            console.error("Failed to load font")
            e.printStackTrace()
        }
        try {
            EMOJI_FONT = FontFamily(
                Font("NotoColorEmoji", resource("fonts/NotoColorEmoji-Regular.ttf").readBytes())
            )
        } catch (e: Throwable) {
            console.error("Failed to load font")
            e.printStackTrace()
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = Typography().run {
            val fontFamily = UNICODE_FONT
            copy(
                displayLarge = displayLarge.copy(fontFamily = fontFamily),
                displayMedium = displayMedium.copy(fontFamily = fontFamily),
                displaySmall = displaySmall.copy(fontFamily = fontFamily),
                headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
                headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
                headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
                titleLarge = titleLarge.copy(fontFamily = fontFamily),
                titleMedium = titleMedium.copy(fontFamily = fontFamily),
                titleSmall = titleSmall.copy(fontFamily = fontFamily),
                bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
                bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
                bodySmall = bodySmall.copy(fontFamily = fontFamily),
                labelLarge = labelLarge.copy(fontFamily = fontFamily),
                labelMedium = labelMedium.copy(fontFamily = fontFamily),
                labelSmall = labelSmall.copy(fontFamily = fontFamily),
            )
        }
    )
}