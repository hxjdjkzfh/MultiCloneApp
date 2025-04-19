package com.multiclone.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Primary brand colors
private val md_theme_light_primary = Color(0xFF006E1C)
private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
private val md_theme_light_primaryContainer = Color(0xFF8BFA80)
private val md_theme_light_onPrimaryContainer = Color(0xFF002105)

private val md_theme_dark_primary = Color(0xFF57DC54)
private val md_theme_dark_onPrimary = Color(0xFF00390B)
private val md_theme_dark_primaryContainer = Color(0xFF005313)
private val md_theme_dark_onPrimaryContainer = Color(0xFF8BFA80)

// Secondary colors
private val md_theme_light_secondary = Color(0xFF006874)
private val md_theme_light_onSecondary = Color(0xFFFFFFFF)
private val md_theme_light_secondaryContainer = Color(0xFF9CEFFF)
private val md_theme_light_onSecondaryContainer = Color(0xFF001F24)

private val md_theme_dark_secondary = Color(0xFF50D8EF)
private val md_theme_dark_onSecondary = Color(0xFF00363D)
private val md_theme_dark_secondaryContainer = Color(0xFF004F58)
private val md_theme_dark_onSecondaryContainer = Color(0xFF9CEFFF)

// Tertiary colors
private val md_theme_light_tertiary = Color(0xFF6750A4)
private val md_theme_light_onTertiary = Color(0xFFFFFFFF)
private val md_theme_light_tertiaryContainer = Color(0xFFEADDFF)
private val md_theme_light_onTertiaryContainer = Color(0xFF21005E)

private val md_theme_dark_tertiary = Color(0xFFCFBCFF)
private val md_theme_dark_onTertiary = Color(0xFF381E72)
private val md_theme_dark_tertiaryContainer = Color(0xFF4F378A)
private val md_theme_dark_onTertiaryContainer = Color(0xFFEADDFF)

// Error colors
private val md_theme_light_error = Color(0xFFBA1A1A)
private val md_theme_light_onError = Color(0xFFFFFFFF)
private val md_theme_light_errorContainer = Color(0xFFFFDAD6)
private val md_theme_light_onErrorContainer = Color(0xFF410002)

private val md_theme_dark_error = Color(0xFFFFB4AB)
private val md_theme_dark_onError = Color(0xFF690005)
private val md_theme_dark_errorContainer = Color(0xFF93000A)
private val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

// Surface colors
private val md_theme_light_background = Color(0xFFF8FDFF)
private val md_theme_light_onBackground = Color(0xFF001F25)
private val md_theme_light_surface = Color(0xFFF8FDFF)
private val md_theme_light_onSurface = Color(0xFF001F25)
private val md_theme_light_surfaceVariant = Color(0xFFDFE4D7)
private val md_theme_light_onSurfaceVariant = Color(0xFF43483F)

private val md_theme_dark_background = Color(0xFF001F25)
private val md_theme_dark_onBackground = Color(0xFFA6EEFF)
private val md_theme_dark_surface = Color(0xFF001F25)
private val md_theme_dark_onSurface = Color(0xFFA6EEFF)
private val md_theme_dark_surfaceVariant = Color(0xFF43483F)
private val md_theme_dark_onSurfaceVariant = Color(0xFFC3C8BB)

// Custom colors for app clones
val CloneBadge = Color(0xFF57DC54)
val CloneCardBackground = Color(0xFFF3F9F0)
val CloneCardBackgroundDark = Color(0xFF1A2418)

// LocalExtendedColors allows us to access our custom colors from anywhere
val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        cloneBadge = CloneBadge,
        cloneCardBackground = CloneCardBackground,
        cloneCardBackgroundDark = CloneCardBackgroundDark
    )
}

/**
 * Custom colors not part of Material3 ColorScheme
 */
data class ExtendedColors(
    val cloneBadge: Color,
    val cloneCardBackground: Color,
    val cloneCardBackgroundDark: Color
)

/**
 * Light theme color scheme
 */
private val LightColorScheme = lightColorScheme(
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
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
)

/**
 * Dark theme color scheme
 */
private val DarkColorScheme = darkColorScheme(
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
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
)

/**
 * Main theme for the MultiClone app that applies Material3 styling
 * and configures status bar color.
 */
@Composable
fun MultiCloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Extended colors based on theme
    val extendedColors = ExtendedColors(
        cloneBadge = CloneBadge,
        cloneCardBackground = if (darkTheme) CloneCardBackgroundDark else CloneCardBackground,
        cloneCardBackgroundDark = CloneCardBackgroundDark
    )
    
    // Configure status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Provide our extended colors along with Material3 theme
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}