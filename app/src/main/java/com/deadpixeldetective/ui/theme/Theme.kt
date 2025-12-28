package com.deadpixeldetective.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Secondary,
    secondary = SecondaryVariant,
    tertiary = Secondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = TextOnPrimary,
    onSecondary = TextOnPrimary,
    onTertiary = TextOnPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AnomalyHighlight,
    onError = TextOnPrimary
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastAccent,
    secondary = HighContrastAccent,
    tertiary = HighContrastAccent,
    background = HighContrastBackground,
    surface = HighContrastBackground,
    onPrimary = HighContrastBackground,
    onSecondary = HighContrastBackground,
    onTertiary = HighContrastBackground,
    onBackground = HighContrastForeground,
    onSurface = HighContrastForeground,
    error = HighContrastAccent,
    onError = HighContrastBackground
)

@Composable
fun DeadPixelDetectiveTheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) HighContrastColorScheme else DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
