package com.multiclone.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Material 3 color scheme for light mode
private val LightColors = lightColorScheme(
    primary = Color(0xFF0D47A1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFE8FF),
    onPrimaryContainer = Color(0xFF001C37),
    secondary = Color(0xFF1565C0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7E3FF),
    onSecondaryContainer = Color(0xFF001B3F),
    tertiary = Color(0xFF2196F3),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFE5FF),
    onTertiaryContainer = Color(0xFF001E31),
    background = Color(0xFFFAFDFD),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFCFCFC),
    onSurface = Color(0xFF1A1C1E),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

// Material 3 color scheme for dark mode
private val DarkColors = darkColorScheme(
    primary = Color(0xFF94CCFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497C),
    onPrimaryContainer = Color(0xFFCDE5FF),
    secondary = Color(0xFFAAC7FF),
    onSecondary = Color(0xFF003063),
    secondaryContainer = Color(0xFF00468A),
    onSecondaryContainer = Color(0xFFD7E3FF),
    tertiary = Color(0xFF93CCFF),
    onTertiary = Color(0xFF003351),
    tertiaryContainer = Color(0xFF004B73),
    onTertiaryContainer = Color(0xFFCBE5FF),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

/**
 * Custom Material3 theme for the MultiClone app
 */
@Composable
fun MultiCloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Use dynamic colors on Android 12+
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    
    // Apply status bar color based on theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}