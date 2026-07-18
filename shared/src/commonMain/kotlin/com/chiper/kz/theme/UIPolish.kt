package com.chiper.kz.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Material You Dynamic Colors support
@Composable
fun DynamicChiperTheme(
    colorScheme: ChiperColorScheme,
    dynamicColorsAvailable: Boolean = false,
    content: @Composable () -> Unit
) {
    // In a real app, use Material3 dynamic colors:
    // val dynamicScheme = if (dynamicColorsAvailable) calculateFromWallpaper() else colorScheme
    
    val typography = DynamicTypography
    
    MaterialTheme(
        colorScheme = colorScheme.toMaterial3Scheme(),
        typography = typography,
        shapes = ChiperShapes,
        content = content
    )
}

object DynamicTypography {
    // Custom fonts for Material You feel
    val fontFamily = FontFamily.Default // In real app: FontFamily(Font(R.font.inter_regular), Font(R.font.inter_medium, FontWeight.Medium), Font(R.font.inter_bold, FontWeight.Bold), Font(R.font.jetbrains_mono_regular, FontWeight.Normal, FontStyle.Normal, FontFamily.Default, FontFamily.Default))
    
    val displayLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    )
    val displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )
    val displaySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    )
    val headlineLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
    val headlineMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    val headlineSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
    val titleLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    val titleMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    val titleSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    val bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    val bodyMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    val bodySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    val labelLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    val labelMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    val labelSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

// RTL Support
@Composable
fun RTLConfiguration(
    isRTL: Boolean,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.platform.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides if (isRTL) androidx.compose.ui.text.style.LayoutDirection.Rtl else androidx.compose.ui.text.style.LayoutDirection.Ltr
    ) {
        content()
    }
}

// Font Scale Support
@Composable
fun FontScaleProvider(
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.platform.CompositionLocalProvider(
        androidx.compose.ui.text.font.FontFamilyResolver.resolveFontFamily provides androidx.compose.ui.text.font.FontFamilyResolver.getDefault(),
        androidx.compose.ui.unit.Density provides androidx.compose.ui.unit.Density { 
            this.density * fontScale 
        }
    ) {
        content()
    }
}

// High Contrast Support
@Composable
fun HighContrastProvider(
    highContrast: Boolean,
    content: @Composable () -> Unit
) {
    val contrastScheme = if (highContrast) HighContrastScheme else ChiperColorScheme.LightDefault
    ChiperTheme(colorScheme = contrastScheme) {
        content()
    }
}

object HighContrastScheme {
    val light = ChiperColorScheme(
        primary = Color(0xFF0000FF),
        onPrimary = Color.White,
        primaryVariant = Color(0xFF0000CC),
        secondary = Color(0xFF00CC00),
        onSecondary = Color.White,
        accent = Color(0xFFFF8800),
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFE0E0E0),
        onSurfaceVariant = Color.Black,
        error = Color(0xFFCC0000),
        outline = Color.Black,
        shadow = Color.Black,
        scrim = Color(0x80000000),
        inverseSurface = Color.Black,
        inverseOnSurface = Color.White,
        inversePrimary = Color(0xFF66AAFF)
    )
    val dark = ChiperColorScheme(
        primary = Color(0xFF66AAFF),
        onPrimary = Color.Black,
        primaryVariant = Color(0xFF3388FF),
        secondary = Color(0xFF66FF66),
        onSecondary = Color.Black,
        accent = Color(0xFFFFAA33),
        background = Color.Black,
        onBackground = Color.White,
        surface = Color(0xFF1A1A1A),
        onSurface = Color.White,
        surfaceVariant = Color(0xFF333333),
        onSurfaceVariant = Color.White,
        error = Color(0xFFFF6666),
        outline = Color.White,
        shadow = Color.White,
        scrim = Color(0x80FFFFFF),
        inverseSurface = Color.White,
        inverseOnSurface = Color.Black,
        inversePrimary = Color(0xFF0055CC)
    )
}

// Landscape Mode Support
@Composable
fun LandscapeAware(
    content: @Composable (isLandscape: Boolean) -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == androidx.compose.ui.platform.Configuration.ORIENTATION_LANDSCAPE
    content(isLandscape)
}

// Chat Density Settings
enum class ChatDensity(val name: String, val multiplier: Float) {
    Compact("Компактная", 0.85f),
    Standard("Стандартная", 1.0f),
    Relaxed("Развёрнутая", 1.15f)
}

@Composable
fun ChatDensityProvider(
    density: ChatDensity = ChatDensity.Standard,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.platform.CompositionLocalProvider(
        androidx.compose.ui.unit.Density provides androidx.compose.ui.unit.Density { 
            this.density * density.multiplier 
        }
    ) {
        content()
    }
}

// Swipe Back Gesture Support
@Composable
fun SwipeBackHandler(
    onBack: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    if (enabled) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitTouchDown()
                    val drag = awaitTouchDrag()
                    if (drag.dragAmount > 100f) {
                        onBack()
                    }
                }
        ) {
            content()
        }
    } else {
        content()
    }
}

// Shared Element Transition (simplified)
@Composable
fun SharedElementTransition(
    key: String,
    content: @Composable () -> Unit
) {
    // In real app, use Modifier.sharedBounds() from Accompanist or custom implementation
    content()
}

// App Shortcuts Support
@Composable
fun AppShortcutsProvider(
    shortcuts: List<AppShortcut>,
    content: @Composable () -> Unit
) {
    // In real app, use Android's ShortcutManager
    content()
}

data class AppShortcut(
    val id: String,
    val shortLabel: String,
    val longLabel: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val intentAction: String
)

// Badge Count Provider
@Composable
fun BadgeProvider(
    unreadCount: Int,
    content: @Composable () -> Unit
) {
    // In real app, use ShortcutBadger or Android's setNumber
    content()
}

// Widget Support
@Composable
fun WidgetDataProvider(
    widgetData: WidgetData,
    content: @Composable () -> Unit
) {
    // In real app, use Glance library for widgets
    content()
}

data class WidgetData(
    val unreadCount: Int,
    val lastMessage: String?,
    val lastMessageTime: Long?
)