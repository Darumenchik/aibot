package com.chiper.kz.theme

import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    System, Light, Dark
}

@kotlinx.serialization.Serializable
data class AppTheme(
    val id: String,
    val name: String,
    val description: String,
    val lightColors: ChiperColorScheme,
    val darkColors: ChiperColorScheme,
    val gradientColors: List<Color> // For animated backgrounds
) {
    companion object {
        val Default = AppTheme(
            id = "ocean",
            name = "Океан",
            description = "Глубокий синий как море",
            lightColors = OceanLightColors,
            darkColors = OceanDarkColors,
            gradientColors = OceanGradient
        )

        val ALL = listOf(
            Default,
            SunsetTheme,
            ForestTheme,
            SpaceTheme,
            NeonTheme,
            AuroraTheme
        )

        fun fromId(id: String): AppTheme = ALL.firstOrNull { it.id == id } ?: Default
    }
}

// ===== OCEAN THEME =====
val OceanGradient = listOf(
    Color(0xFF0D1B2A),
    Color(0xFF1B263B),
    Color(0xFF2E4053),
    Color(0xFF172A3A)
)

val OceanLightColors = ChiperColorScheme(
    primary = Color(0xFF2AABEE),
    onPrimary = Color.White,
    primaryVariant = Color(0xFF1A73E8),
    secondary = Color(0xFF5EB5F7),
    onSecondary = Color.White,
    accent = Color(0xFF4DCD5E),
    background = Color(0xFFF5F9FF),
    onBackground = Color(0xFF0D1B2A),
    surface = Color.White,
    onSurface = Color(0xFF0D1B2A),
    surfaceVariant = Color(0xFFE3F2FD),
    onSurfaceVariant = Color(0xFF1B263B).copy(alpha = 0.7f),
    error = Color(0xFFE53935),
    outline = Color(0xFFB0C4DE),
    shadow = Color(0x1A0D1B2A),
    scrim = Color(0x4D0D1B2A),
    inverseSurface = Color(0xFF1B263B),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF5EB5F7)
)

val OceanDarkColors = ChiperColorScheme(
    primary = Color(0xFF5EB5F7),
    onPrimary = Color(0xFF0D1B2A),
    primaryVariant = Color(0xFF2AABEE),
    secondary = Color(0xFF2AABEE),
    onSecondary = Color.White,
    accent = Color(0xFF4DCD5E),
    background = Color(0xFF0D1B2A),
    onBackground = Color.White,
    surface = Color(0xFF16213E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B263B),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFEF5350),
    outline = Color(0xFF2E4053),
    shadow = Color(0x33000000),
    scrim = Color(0x66000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF0D1B2A),
    inversePrimary = Color(0xFF2AABEE)
)

// ===== SUNSET THEME =====
val SunsetGradient = listOf(
    Color(0xFF2D1B3D),
    Color(0xFF4A235A),
    Color(0xFF6B2D6D),
    Color(0xFF3D1A4A)
)

val SunsetLightColors = ChiperColorScheme(
    primary = Color(0xFFE91E63),
    onPrimary = Color.White,
    primaryVariant = Color(0xFFC2185B),
    secondary = Color(0xFFF06292),
    onSecondary = Color.White,
    accent = Color(0xFFFBBC05),
    background = Color(0xFFFFF0F5),
    onBackground = Color(0xFF2D1B3D),
    surface = Color.White,
    onSurface = Color(0xFF2D1B3D),
    surfaceVariant = Color(0xFFFCE4EC),
    onSurfaceVariant = Color(0xFF4A235A).copy(alpha = 0.7f),
    error = Color(0xFFE53935),
    outline = Color(0xFFF8BBD0),
    shadow = Color(0x1A2D1B3D),
    scrim = Color(0x4D2D1B3D),
    inverseSurface = Color(0xFF4A235A),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFFF06292)
)

val SunsetDarkColors = ChiperColorScheme(
    primary = Color(0xFFF06292),
    onPrimary = Color(0xFF2D1B3D),
    primaryVariant = Color(0xFFE91E63),
    secondary = Color(0xFFE91E63),
    onSecondary = Color.White,
    accent = Color(0xFFFBBC05),
    background = Color(0xFF2D1B3D),
    onBackground = Color.White,
    surface = Color(0xFF3D1A4A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF4A235A),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFEF5350),
    outline = Color(0xFF6B2D6D),
    shadow = Color(0x33000000),
    scrim = Color(0x66000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF2D1B3D),
    inversePrimary = Color(0xFFE91E63)
)

// ===== FOREST THEME =====
val ForestGradient = listOf(
    Color(0xFF0D2818),
    Color(0xFF1B4D2E),
    Color(0xFF2E7D32),
    Color(0xFF145214)
)

val ForestLightColors = ChiperColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryVariant = Color(0xFF1B5E20),
    secondary = Color(0xFF4CAF50),
    onSecondary = Color.White,
    accent = Color(0xFFFF9800),
    background = Color(0xFFE8F5E9),
    onBackground = Color(0xFF0D2818),
    surface = Color.White,
    onSurface = Color(0xFF0D2818),
    surfaceVariant = Color(0xFFC8E6C9),
    onSurfaceVariant = Color(0xFF1B4D2E).copy(alpha = 0.7f),
    error = Color(0xFFE53935),
    outline = Color(0xFFA5D6A7),
    shadow = Color(0x1A0D2818),
    scrim = Color(0x4D0D2818),
    inverseSurface = Color(0xFF1B4D2E),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF4CAF50)
)

val ForestDarkColors = ChiperColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color(0xFF0D2818),
    primaryVariant = Color(0xFF2E7D32),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    accent = Color(0xFFFF9800),
    background = Color(0xFF0D2818),
    onBackground = Color.White,
    surface = Color(0xFF145214),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B4D2E),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFEF5350),
    outline = Color(0xFF2E7D32),
    shadow = Color(0x33000000),
    scrim = Color(0x66000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF0D2818),
    inversePrimary = Color(0xFF2E7D32)
)

// ===== SPACE THEME =====
val SpaceGradient = listOf(
    Color(0xFF0A0A1A),
    Color(0xFF1A1A3A),
    Color(0xFF2A2A5A),
    Color(0xFF151530)
)

val SpaceLightColors = ChiperColorScheme(
    primary = Color(0xFF6C5CE7),
    onPrimary = Color.White,
    primaryVariant = Color(0xFF5A4BD4),
    secondary = Color(0xFFA29BFE),
    onSecondary = Color(0xFF0A0A1A),
    accent = Color(0xFF00CEC9),
    background = Color(0xFFF0EEFB),
    onBackground = Color(0xFF0A0A1A),
    surface = Color.White,
    onSurface = Color(0xFF0A0A1A),
    surfaceVariant = Color(0xFFE1DFF8),
    onSurfaceVariant = Color(0xFF1A1A3A).copy(alpha = 0.7f),
    error = Color(0xFFE53935),
    outline = Color(0xFFB2AEE0),
    shadow = Color(0x1A0A0A1A),
    scrim = Color(0x4D0A0A1A),
    inverseSurface = Color(0xFF1A1A3A),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFFA29BFE)
)

val SpaceDarkColors = ChiperColorScheme(
    primary = Color(0xFFA29BFE),
    onPrimary = Color(0xFF0A0A1A),
    primaryVariant = Color(0xFF6C5CE7),
    secondary = Color(0xFF6C5CE7),
    onSecondary = Color.White,
    accent = Color(0xFF00CEC9),
    background = Color(0xFF0A0A1A),
    onBackground = Color.White,
    surface = Color(0xFF151530),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A3A),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFEF5350),
    outline = Color(0xFF2A2A5A),
    shadow = Color(0x33000000),
    scrim = Color(0x66000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF0A0A1A),
    inversePrimary = Color(0xFF6C5CE7)
)

// ===== NEON THEME =====
val NeonGradient = listOf(
    Color(0xFF1A0033),
    Color(0xFF330066),
    Color(0xFF4D0099),
    Color(0xFF1F0040)
)

val NeonLightColors = ChiperColorScheme(
    primary = Color(0xFFD500F9),
    onPrimary = Color.White,
    primaryVariant = Color(0xFFAA00FF),
    secondary = Color(0xFF00E5FF),
    onSecondary = Color(0xFF1A0033),
    accent = Color(0xFFFFEA00),
    background = Color(0xFFF8E8FC),
    onBackground = Color(0xFF1A0033),
    surface = Color.White,
    onSurface = Color(0xFF1A0033),
    surfaceVariant = Color(0xFFECCBF9),
    onSurfaceVariant = Color(0xFF330066).copy(alpha = 0.7f),
    error = Color(0xFFE53935),
    outline = Color(0xFFD5A5F5),
    shadow = Color(0x1A1A0033),
    scrim = Color(0x4D1A0033),
    inverseSurface = Color(0xFF330066),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFFE040FB)
)

val NeonDarkColors = ChiperColorScheme(
    primary = Color(0xFFE040FB),
    onPrimary = Color(0xFF1A0033),
    primaryVariant = Color(0xFFD500F9),
    secondary = Color(0xFF00E5FF),
    onSecondary = Color(0xFF1A0033),
    accent = Color(0xFFFFEA00),
    background = Color(0xFF1A0033),
    onBackground = Color.White,
    surface = Color(0xFF2D004D),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF330066),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFEF5350),
    outline = Color(0xFF4D0099),
    shadow = Color(0x33000000),
    scrim = Color(0x66000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF1A0033),
    inversePrimary = Color(0xFFD500F9)
)

// ===== AURORA THEME =====
val AuroraGradient = listOf(
    Color(0xFF0A1F2E),
    Color(0xFF123852),
    Color(0xFF0A5C75),
    Color(0xFF042E42)
)

val AuroraLightColors = ChiperColorScheme(
    primary = Color(0xFF00B4D8),
    onPrimary = Color.White,
    primaryVariant = Color(0xFF0096C7),
    secondary = Color(0xFF90E0EF),
    onSecondary = Color(0xFF0A1F2E),
    accent = Color(0xFFFFB700),
    background = Color(0xFFE0F7FA),
    onBackground = Color(0xFF0A1F2E),
    surface = Color.White,
    onSurface = Color(0xFF0A1F2E),
    surfaceVariant = Color(0xFFB2EBF2),
    onSurfaceVariant = Color(0xFF123852).copy(alpha = 0.7f),
    error = Color(0xFFE53935),
    outline = Color(0xFF80DEEA),
    shadow = Color(0x1A0A1F2E),
    scrim = Color(0x4D0A1F2E),
    inverseSurface = Color(0xFF123852),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF90E0EF)
)

val AuroraDarkColors = ChiperColorScheme(
    primary = Color(0xFF90E0EF),
    onPrimary = Color(0xFF0A1F2E),
    primaryVariant = Color(0xFF00B4D8),
    secondary = Color(0xFF00B4D8),
    onSecondary = Color.White,
    accent = Color(0xFFFFB700),
    background = Color(0xFF0A1F2E),
    onBackground = Color.White,
    surface = Color(0xFF123852),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A4A6E),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFEF5350),
    outline = Color(0xFF0A5C75),
    shadow = Color(0x33000000),
    scrim = Color(0x66000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF0A1F2E),
    inversePrimary = Color(0xFF00B4D8)
)