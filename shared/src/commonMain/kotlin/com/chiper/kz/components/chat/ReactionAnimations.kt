package com.chiper.kz.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.theme.glass.GlassSurface
import com.chiper.kz.theme.glass.GlassElevation
import com.chiper.kz.theme.glass.GlassShapes
import kotlinx.coroutines.delay

@Composable
fun ReactionExplosion(
    emoji: String,
    position: Offset,
    onComplete: () -> Unit
) {
    val particles = remember { (1..15).map { Particle() } }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(800)
        isVisible = false
        delay(100)
        onComplete()
    }

    if (!isVisible) return

    Box(
        modifier = Modifier
            .offset(x = position.x.dp, y = position.y.dp)
            .size(1.dp)
    ) {
        particles.forEach { particle ->
            var progress by remember { mutableStateOf(0f) }
            LaunchedEffect(particle.id) {
                delay((particle.id * 20).toLong())
                val anim = animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 600 + particle.id * 30,
                        easing = FastOutSlowInEasing
                    )
                ) { value, _ ->
                    progress = value
                }
            }

            val currentX = particle.direction.x * progress * particle.maxDistance
            val currentY = particle.direction.y * progress * particle.maxDistance - progress * particle.gravity * 200
            val currentScale = 1f - progress * 0.5f
            val currentAlpha = 1f - progress
            val currentRotation = particle.rotationSpeed * progress * 360

            Text(
                text = emoji,
                fontSize = (particle.size * currentScale).sp,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = currentX
                        translationY = currentY
                        scaleX = currentScale
                        scaleY = currentScale
                        alpha = currentAlpha
                        rotationZ = currentRotation
                    }
            )
        }
    }
}

data class Particle(
    val id: Int,
    val direction: Offset = Offset(
        (Math.random() - 0.5) * 2,
        -(Math.random() * 0.8 + 0.2)
    ),
    val maxDistance: Float = (Math.random() * 100 + 50).toFloat(),
    val gravity: Float = (Math.random() * 0.5 + 0.5).toFloat(),
    val size: Float = (Math.random() * 12 + 18).toFloat(),
    val rotationSpeed: Float = (Math.random() - 0.5).toFloat()
)

@Composable
fun FloatingReaction(
    emoji: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        scale = 1f
        alpha = 1f
        delay(2000)
        alpha = 0f
        offsetY = -100f
        delay(300)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.translationY = offsetY
                this.alpha = alpha
            }
            .size(80.dp)
            .background(
                Color.White.copy(alpha = 0.9f),
                CircleShape
            )
            .wrapContentSize(Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 40.sp)
    }
}

@Composable
fun ReactionBurst(
    emoji: String,
    centerX: Float,
    centerY: Float,
    onComplete: () -> Unit
) {
    val particles = remember { (1..12).map { BurstParticle() } }

    Box(
        modifier = Modifier
            .offset(x = centerX.dp, y = centerY.dp)
            .size(1.dp)
    ) {
        particles.forEach { particle ->
            var progress by remember { mutableStateOf(0f) }

            LaunchedEffect(particle.id) {
                val anim = animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500 + particle.id * 20,
                        easing = FastOutSlowInEasing
                    )
                ) { value, _ ->
                    progress = value
                    if (value >= 1f && particle.id == particles.last().id) {
                        delay(100)
                        onComplete()
                    }
                }
            }

            val radius = progress * particle.maxRadius
            val angle = particle.angle + progress * particle.spin * 360
            val x = radius * kotlin.math.cos(kotlin.math.toRadians(angle.toDouble()))
            val y = radius * kotlin.math.sin(kotlin.math.toRadians(angle.toDouble())) - progress * 150
            val scale = 1f - progress * 0.6f
            val alpha = 1f - progress

            Text(
                text = emoji,
                fontSize = (particle.size * scale).sp,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = x
                        translationY = y
                        scaleX = scale
                        scaleY = scale
                        alpha = alpha
                    }
            )
        }
    }
}

data class BurstParticle(
    val id: Int = Math.random().toInt(),
    val angle: Float = Math.random() * 360,
    val maxRadius: Float = (Math.random() * 80 + 40).toFloat(),
    val spin: Float = (Math.random() - 0.5).toFloat() * 2,
    val size: Float = (Math.random() * 10 + 20).toFloat()
)