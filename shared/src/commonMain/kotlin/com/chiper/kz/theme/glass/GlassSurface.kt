package com.chiper.kz.theme.glass

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.px

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = GlassShapes.Card,
    elevation: GlassElevation = GlassElevation.Level1,
    tint: Color = GlassColors.SurfaceTint,
    content: @Composable () -> Unit
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = elevation.surfaceAlpha),
            Color.White.copy(alpha = elevation.surfaceAlpha * 0.3f)
        ),
        start = Offset.Zero,
        end = Offset(0f, 200f)
    )

    val borderGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = elevation.borderAlpha),
            Color.White.copy(alpha = elevation.borderAlpha * 0.2f)
        ),
        start = Offset.Zero,
        end = Offset(0f, 200f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = elevation.backgroundAlpha),
                        Color.White.copy(alpha = elevation.backgroundAlpha * 0.5f)
                    )
                ),
                shape = shape
            )
            .clip(shape)
            .drawBehind {
                drawRect(
                    brush = gradient,
                    topLeft = Offset.Zero,
                    size = size,
                    style = Fill,
                    blendMode = BlendMode.SrcOver
                )
            }
            .drawWithContent()
            .drawWithCache {
                onDrawWithContent()
                drawRect(
                    brush = borderGradient,
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = elevation.borderWidth.toPx()),
                    blendMode = BlendMode.SrcOver
                )
            }
    ) {
        content()
    }
}

data class GlassElevation(
    val surfaceAlpha: Float,
    val backgroundAlpha: Float,
    val borderAlpha: Float,
    val borderWidth: Dp,
    val shadowColor: Color,
    val shadowElevation: Dp
) {
    companion object {
        val Level0 = GlassElevation(0.04f, 0.02f, 0.06f, 0.5.dp, Color.Transparent, 0.dp)
        val Level1 = GlassElevation(0.08f, 0.05f, 0.12f, 1.dp, Color(0x1A000000), 4.dp)
        val Level2 = GlassElevation(0.12f, 0.08f, 0.18f, 1.5.dp, Color(0x26000000), 8.dp)
        val Level3 = GlassElevation(0.18f, 0.12f, 0.25f, 2.dp, Color(0x33000000), 16.dp)
        val Level4 = GlassElevation(0.25f, 0.18f, 0.35f, 2.5.dp, Color(0x40000000), 24.dp)
        val Floating = GlassElevation(0.15f, 0.1f, 0.22f, 1.dp, Color(0x3D000000), 32.dp)
        val Modal = GlassElevation(0.2f, 0.15f, 0.3f, 1.5.dp, Color(0x4D000000), 40.dp)
    }
}

object GlassShapes {
    val Card = RoundedCornerShape(24.dp)
    val Button = RoundedCornerShape(16.dp)
    val Field = RoundedCornerShape(14.dp)
    val Chip = RoundedCornerShape(100.dp)
    val Modal = RoundedCornerShape(28.dp)
    val Island = RoundedCornerShape(32.dp)
    val Bubble = RoundedCornerShape(20.dp)
    val Small = RoundedCornerShape(12.dp)
}

object GlassColors {
    val SurfaceTint = Color.White.copy(alpha = 0.1f)
    val PrimaryTint = Color(0xFF2AABEE).copy(alpha = 0.15f)
    val SuccessTint = Color(0xFF4DCD5E).copy(alpha = 0.15f)
    val ErrorTint = Color(0xFFE53935).copy(alpha = 0.15f)
    val WarningTint = Color(0xFFFBBC05).copy(alpha = 0.15f)
}

@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = GlassGradients.MainBackground,
    animated: Boolean = false
) {
    if (animated) {
        AnimatedBackground(modifier, colors)
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = colors
                    )
                )
        )
    }
}

@Composable
private fun AnimatedBackground(
    modifier: Modifier,
    colors: List<Color>
) {
    val transition = rememberInfiniteTransition(label = "bg_animation")
    val offset1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )
    val offset2 by transition.animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset2"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        center = Offset(
                            size.width * (0.3f + 0.4f * sin(offset1 * Math.PI / 180)),
                            size.height * (0.2f + 0.3f * cos(offset1 * Math.PI / 180))
                        ),
                        radius = max(size.width, size.height) * 0.7f,
                        colors = colors.map { it.copy(alpha = it.alpha * 0.4f) }
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        center = Offset(
                            size.width * (0.7f + 0.2f * cos(offset2 * Math.PI / 180)),
                            size.height * (0.8f + 0.1f * sin(offset2 * Math.PI / 180))
                        ),
                        radius = max(size.width, size.height) * 0.6f,
                        colors = colors.map { it.copy(alpha = it.alpha * 0.3f) }
                    )
                )
        )
    }
}

object GlassGradients {
    val MainBackground = listOf(
        Color(0xFF0A0E27),
        Color(0xFF1A1F3A),
        Color(0xFF2D2F4F),
        Color(0xFF16213E)
    )
    val WarmBackground = listOf(
        Color(0xFF2C1810),
        Color(0xFF4A2A20),
        Color(0xFF6B3A2E),
        Color(0xFF3D2317)
    )
    val CoolBackground = listOf(
        Color(0xFF0D1B2A),
        Color(0xFF1B263B),
        Color(0xFF2E4053),
        Color(0xFF172A3A)
    )
    val AuroraBackground = listOf(
        Color(0xFF0A1F2E),
        Color(0xFF123852),
        Color(0xFF0A5C75),
        Color(0xFF042E42)
    )
    val SunsetBackground = listOf(
        Color(0xFF2D1B3D),
        Color(0xFF4A235A),
        Color(0xFF6B2D6D),
        Color(0xFF3D1A4A)
    )
    val OceanBackground = listOf(
        Color(0xFF001F3F),
        Color(0xFF003D6B),
        Color(0xFF005F9E),
        Color(0xFF002E5D)
    )
    val ForestBackground = listOf(
        Color(0xFF0D2818),
        Color(0xFF1B4D2E),
        Color(0xFF2E7D32),
        Color(0xFF145214)
    )
    val SpaceBackground = listOf(
        Color(0xFF0A0A1A),
        Color(0xFF1A1A3A),
        Color(0xFF2A2A5A),
        Color(0xFF151530)
    )
    val NeonBackground = listOf(
        Color(0xFF1A0033),
        Color(0xFF330066),
        Color(0xFF4D0099),
        Color(0xFF1F0040)
    )
}

private fun sin(degrees: Float): Float = kotlin.math.sin(degrees * kotlin.math.PI / 180.0)
private fun cos(degrees: Float): Float = kotlin.math.cos(degrees * kotlin.math.PI / 180.0)