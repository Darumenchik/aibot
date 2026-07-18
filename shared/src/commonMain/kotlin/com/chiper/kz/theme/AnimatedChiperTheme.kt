package com.chiper.kz.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.theme.AppTheme

@Composable
fun AnimatedChiperTheme(
    viewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val currentTheme by viewModel.appTheme.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isAnimating by viewModel.isAnimating.collectAsState()

    // Calculate effective theme based on mode
    val effectiveTheme = remember(currentTheme, themeMode) {
        when (themeMode) {
            is ThemeMode.Light -> currentTheme.lightColors
            is ThemeMode.Dark -> currentTheme.darkColors
            is ThemeMode.System -> {
                // TODO: Detect system theme
                currentTheme.lightColors
            }
        }
    }

    // Animate color transitions
    val animatedPrimary by animateColorAsState(
        targetValue = effectiveTheme.primary,
        animationSpec = if (isAnimating) tween(400) else spring(dampingRatio = 0.8f, stiffness = 200f)
    )
    val animatedBackground by animateColorAsState(
        targetValue = effectiveTheme.background,
        animationSpec = if (isAnimating) tween(400) else spring(dampingRatio = 0.8f, stiffness = 200f)
    )
    val animatedSurface by animateColorAsState(
        targetValue = effectiveTheme.surface,
        animationSpec = if (isAnimating) tween(400) else spring(dampingRatio = 0.8f, stiffness = 200f)
    )
    val animatedAccent by animateColorAsState(
        targetValue = effectiveTheme.accent,
        animationSpec = if (isAnimating) tween(400) else spring(dampingRatio = 0.8f, stiffness = 200f)
    )
    val animatedOnBackground by animateColorAsState(
        targetValue = effectiveTheme.onBackground,
        animationSpec = if (isAnimating) tween(400) else spring(dampingRatio = 0.8f, stiffness = 200f)
    )
    val animatedOnSurface by animateColorAsState(
        targetValue = effectiveTheme.onSurface,
        animationSpec = if (isAnimating) tween(400) else spring(dampingRatio = 0.8f, stiffness = 200f)
    )

    // Create animated color scheme
    val animatedScheme = remember(effectiveTheme, animatedPrimary, animatedBackground, animatedSurface, animatedAccent, animatedOnBackground, animatedOnSurface) {
        ChiperColorScheme(
            primary = animatedPrimary,
            onPrimary = effectiveTheme.onPrimary,
            primaryVariant = effectiveTheme.primaryVariant,
            secondary = effectiveTheme.secondary,
            onSecondary = effectiveTheme.onSecondary,
            accent = animatedAccent,
            background = animatedBackground,
            onBackground = animatedOnBackground,
            surface = animatedSurface,
            onSurface = animatedOnSurface,
            surfaceVariant = effectiveTheme.surfaceVariant,
            onSurfaceVariant = effectiveTheme.onSurfaceVariant,
            error = effectiveTheme.error,
            outline = effectiveTheme.outline,
            shadow = effectiveTheme.shadow,
            scrim = effectiveTheme.scrim,
            inverseSurface = effectiveTheme.inverseSurface,
            inverseOnSurface = effectiveTheme.inverseOnSurface,
            inversePrimary = effectiveTheme.inversePrimary
        )
    }

    ChiperTheme(colorScheme = animatedScheme) {
        content()
    }
}