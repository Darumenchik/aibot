package com.chiper.kz.theme

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class ChiperColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val onSecondary: Color,
    val accent: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val error: Color,
    val outline: Color,
    val shadow: Color,
    val scrim: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color
) {
    companion object {
        val LightDefault = OceanLightColors
        val DarkDefault = OceanDarkColors
    }
}

// Material3 ColorScheme extensions
internal fun ChiperColorScheme.toMaterialLight() = androidx.compose.material3.lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryVariant,
    onPrimaryContainer = onPrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = surfaceVariant,
    onSecondaryContainer = onSurface,
    tertiary = accent,
    onTertiary = onPrimary,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    error = error,
    onError = Color.White,
    outline = outline,
    surfaceContainerHighest = surfaceVariant,
)

internal fun ChiperColorScheme.toMaterialDark() = androidx.compose.material3.darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryVariant,
    onPrimaryContainer = onPrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = surfaceVariant,
    onSecondaryContainer = onSurface,
    tertiary = accent,
    onTertiary = onPrimary,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    error = error,
    onError = Color.Black,
    outline = outline,
    surfaceContainerHighest = surfaceVariant,
)