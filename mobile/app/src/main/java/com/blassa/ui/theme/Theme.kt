package com.blassa.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = CardLight,
    primaryContainer = TealLight,
    onPrimaryContainer = TealDark,
    
    secondary = AmberAction,
    onSecondary = TextPrimaryLight,
    secondaryContainer = AmberLight,
    onSecondaryContainer = TextPrimaryLight,
    
    tertiary = FemaleOnly,
    onTertiary = CardLight,
    tertiaryContainer = FemaleOnlyLight,
    onTertiaryContainer = FemaleOnly,
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextMutedLight,
    
    error = Error,
    onError = CardLight,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    
    outline = TextMutedLight,
    outlineVariant = SurfaceVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = BackgroundDark,
    primaryContainer = TealPrimary,
    onPrimaryContainer = TealLight,
    
    secondary = AmberActionDark,
    onSecondary = BackgroundDark,
    secondaryContainer = AmberAction,
    onSecondaryContainer = AmberLight,
    
    tertiary = FemaleOnly,
    onTertiary = CardLight,
    tertiaryContainer = FemaleOnly,
    onTertiaryContainer = FemaleOnlyLight,
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextMutedDark,
    
    error = Error,
    onError = CardLight,
    errorContainer = Error,
    onErrorContainer = ErrorLight,
    
    outline = TextMutedDark,
    outlineVariant = SurfaceVariantDark
)

@Composable
fun BlassaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+, but we use our brand colors
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) BackgroundDark.toArgb() else TealPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BlassaTypography,
        content = content
    )
}
