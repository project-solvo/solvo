package org.solvo.web.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import org.jetbrains.compose.resources.resource


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

var UNICODE_FONT by mutableStateOf<FontFamily?>(null)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
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
                Font("FangZhengHeiTiSC", resource("fonts/FangZhengHeiTiSC.ttf").readBytes()),
//            Font("FangZhengHeiTiTC", resource("fonts/FangZhengHeiTiTC.ttf").readBytes()),
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