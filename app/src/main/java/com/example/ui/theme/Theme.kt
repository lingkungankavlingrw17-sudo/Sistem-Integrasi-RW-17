package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CivicBluePrimaryDark,
    onPrimary = CivicBlueOnPrimaryDark,
    primaryContainer = CivicBlueContainerDark,
    onPrimaryContainer = CivicBlueOnContainerDark,
    secondary = CivicEmeraldSecondaryDark,
    onSecondary = CivicEmeraldOnSecondaryDark,
    secondaryContainer = CivicEmeraldContainerDark,
    onSecondaryContainer = CivicEmeraldOnContainerDark,
    tertiary = CivicAmberTertiaryDark,
    onTertiary = CivicAmberOnTertiaryDark,
    tertiaryContainer = CivicAmberContainerDark,
    onTertiaryContainer = CivicAmberOnContainerDark,
    background = CivicBackgroundDark,
    onBackground = CivicOnBackgroundDark,
    surface = CivicSurfaceDark,
    onSurface = CivicOnSurfaceDark,
    surfaceVariant = CivicSurfaceVariantDark,
    onSurfaceVariant = CivicOnSurfaceVariantDark,
    outline = CivicOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = CivicBluePrimary,
    onPrimary = CivicBlueOnPrimary,
    primaryContainer = CivicBlueContainer,
    onPrimaryContainer = CivicBlueOnContainer,
    secondary = CivicEmeraldSecondary,
    onSecondary = CivicEmeraldOnSecondary,
    secondaryContainer = CivicEmeraldContainer,
    onSecondaryContainer = CivicEmeraldOnContainer,
    tertiary = CivicAmberTertiary,
    onTertiary = CivicAmberOnTertiary,
    tertiaryContainer = CivicAmberContainer,
    onTertiaryContainer = CivicAmberOnContainer,
    background = CivicBackground,
    onBackground = CivicOnBackground,
    surface = CivicSurface,
    onSurface = CivicOnSurface,
    surfaceVariant = CivicSurfaceVariant,
    onSurfaceVariant = CivicOnSurfaceVariant,
    outline = CivicOutline,
    outlineVariant = CivicOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep bespoke civic branding consistent
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
