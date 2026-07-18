package com.chiper.kz.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.canvas.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.theme.glass.GlassSurface
import com.chiper.kz.theme.glass.GlassElevation
import com.chiper.kz.theme.glass.GlassShapes
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType

@Composable
fun GlassVoiceMessage(
    message: com.chiper.kz.model.Message,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReply: (() -> Unit)? = null,
    onReactionClick: ((com.chiper.kz.model.Message, String) -> Unit)? = null,
    onReactionAdd: ((com.chiper.kz.model.Message, String) -> Unit)? = null,
    showTime: Boolean = true,
    showStatus: Boolean = true,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isSent = message.isSentByMe
    val haptic = HapticFeedback.rememberHaptic()
    val duration = message.voiceDuration.let { if (it > 0 ) it else 30 }
    var progress by remember { mutableStateOf(0f) }
    var animatedProgress by remember { mutableStateOf(0f) }
    var wavePhase by remember { mutableStateOf(0f) }

    // Animate wave phase
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val anim = animate(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) { value, _ ->
                wavePhase = value
            }
        }
    }

    // Animate progress
    LaunchedEffect(isPlaying, progress) {
        if (isPlaying) {
            val anim = animate(
                initialValue = progress,
                targetValue = 1f,
                animationSpec = tween(durationMillis = duration * 1000, easing = LinearEasing)
            ) { value, _ ->
                animatedProgress = value
            }
        }
    }

    val bubbleColor = if (isSent) 
        com.chiper.kz.theme.MaterialTheme.colorScheme.primaryContainer 
    else 
        com.chiper.kz.theme.MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (isSent) 
        com.chiper.kz.theme.MaterialTheme.colorScheme.onPrimaryContainer 
    else 
        com.chiper.kz.theme.MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isSent) 64.dp else 8.dp,
                end = if (isSent) 8.dp else 64.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
        horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 180.dp, max = 320.dp)
                .clip(RoundedCornerShape(
                    topStart = if (isSent) 20.dp else 4.dp,
                    topEnd = if (isSent) 4.dp else 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                ))
                .background(bubbleColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (isPlaying) onPause() else onPlay()
                    haptic.trigger(HapticType.Selection)
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isPlaying) 
                                com.chiper.kz.theme.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                            else 
                                com.chiper.kz.theme.MaterialTheme.colorScheme.primary,
                            GlassShapes.Circle
                        )
                        .graphicsLayer {
                            scaleX = if (isPlaying) 1.1f else 1f
                            scaleY = if (isPlaying) 1.1f else 1f
                        }
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(visible = isPlaying) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = com.chiper.kz.theme.MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    AnimatedVisibility(visible = !isPlaying) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = if (isSent) com.chiper.kz.theme.MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Waveform visualization
                VoiceWaveform(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .fillMaxWidth(),
                    isPlaying = isPlaying,
                    progress = if (isPlaying) animatedProgress else progress,
                    wavePhase = wavePhase,
                    color = if (isPlaying) com.chiper.kz.theme.MaterialTheme.colorScheme.primary else textColor.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Duration
                Text(
                    text = formatDuration(duration),
                    fontSize = 13.sp,
                    color = textColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Reactions panel
        if (message.reactions.isNotEmpty()) {
            AnimatedVisibility(
                visible = message.reactions.isNotEmpty(),
                enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)) + fadeOut()
            ) {
                MessageReactions(
                    reactions = message.reactions,
                    onReactionClick = onReactionClick,
                    onReactionAdd = onReactionAdd,
                    message = message,
                    isSent = isSent
                )
            }
        }

        // Time and status
        if (showTime || showStatus) {
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showTime) {
                    Text(
                        text = formatMessageTime(message.timestamp),
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
                if (showStatus && isSent) {
                    Icon(
                        imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                        contentDescription = if (message.isRead) "Read" else "Sent",
                        tint = if (message.isRead) com.chiper.kz.theme.MaterialTheme.colorScheme.primary else textColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceWaveform(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    progress: Float,
    wavePhase: Float,
    color: Color = com.chiper.kz.theme.MaterialTheme.colorScheme.primary,
    barCount: Int = 40
) {
    val bars = remember { (1..barCount).map { WaveBar() } }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val barWidth = width / barCount
        val spacing = barWidth * 0.3f
        val barW = barWidth - spacing

        bars.forEachIndexed { index, bar ->
            val x = index * barWidth + spacing / 2 + barW / 2

            var currentHeight: Float
            if (isPlaying) {
                val waveInfluence = kotlin.math.sin(kotlin.math.toRadians(wavePhase + index * 15))
                val baseHeight = bar.baseHeight * height * 0.8f
                val waveHeight = abs(waveInfluence) * bar.amplitude * height * 0.4f
                currentHeight = (baseHeight + waveHeight).coerceAtMost(height * 0.9f)
            } else {
                currentHeight = bar.baseHeight * height * 0.4f * (1f - progress * 0.3f)
            }

            val y1 = centerY - currentHeight / 2
            val y2 = centerY + currentHeight / 2

            val barColor = color.copy(alpha = if (isPlaying) 1f else 0.5f)

            drawLine(
                color = barColor,
                start = Offset(x, y1),
                end = Offset(x, y2),
                strokeWidth = barW,
                cap = StrokeCap.Round
            )
        }
    }
}

data class WaveBar(
    val baseHeight: Float = (Math.random() * 0.6 + 0.2).toFloat(),
    val amplitude: Float = (Math.random() * 0.5 + 0.5).toFloat()
)

fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

fun formatMessageTime(timestamp: Long): String {
    val cal = timestamp / 1000
    val hours = (cal / 3600) % 24
    val minutes = (cal / 60) % 60
    return "%02d:%02d".format(hours, minutes)
}