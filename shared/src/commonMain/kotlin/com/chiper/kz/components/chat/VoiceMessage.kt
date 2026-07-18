package com.chiper.kz.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.model.Message
import com.chiper.kz.theme.glass.GlassSurface
import com.chiper.kz.theme.glass.GlassElevation
import com.chiper.kz.theme.glass.GlassShapes
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType
import com.chiper.kz.utils.rememberHapticFeedback
import kotlinx.coroutines.delay

@Composable
fun VoiceMessageBubble(
    message: Message,
    onPlayClick: () -> Unit,
    onLongPress: (() -> Unit)? = nil,
    isPlaying: Boolean = false,
    currentPosition: Float = 0f, // 0 to 1
    modifier: Modifier = Modifier
) {
    val isSent = message.isSentByMe
    val haptic = rememberHapticFeedback()
    val duration = message.voiceDuration
    val formattedDuration = formatDuration(duration)

    var waveAnimProgress by remember { mutableStateOf(0f) }
    var pulseAnimProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            waveAnimProgress = 0f
            pulseAnimProgress = 0f
        }
    }

    // Wave animation
    LaunchedEffect(Unit) {
        if (isPlaying) {
            infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        }
    }.also {
        if (isPlaying) {
            while (isPlaying) {
                waveAnimProgress = (waveAnimProgress + 0.02f) % 1f
                pulseAnimProgress = (pulseAnimProgress + 0.015f) % 1f
                delay(16)
            }
        }
    }

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
        GlassSurface(
            modifier = Modifier
                .widthIn(min = 160.dp, max = 280.dp)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            elevation = GlassElevation.Level1
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause button with pulse animation
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer {
                            scaleX = 1f + pulseAnimProgress * 0.15f
                            scaleY = 1f + pulseAnimProgress * 0.15f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            haptic.trigger(HapticType.Selection)
                            onPlayClick()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        AnimatedContent(
                            targetState = isPlaying,
                            transitionSpec = {
                                fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) togetherWith
                                scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) togetherWith
                                fadeOut(animationSpec = tween(150)) togetherWith
                                scaleOut(animationSpec = tween(150))
                            }
                        ) { target ->
                            Icon(
                                imageVector = if (target) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (target) "Pause" else "Play",
                                tint = if (isSent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Waveform visualization
                ExpandedWaveform(
                    isPlaying = isPlaying,
                    progress = currentPosition,
                    waveAnimProgress = waveAnimProgress,
                    barCount = 30,
                    color = if (isSent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Duration
                Text(
                    text = formattedDuration,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSent) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ExpandedWaveform(
    isPlaying: Boolean,
    progress: Float,
    waveAnimProgress: Float,
    barCount: Int = 30,
    color: Color,
    modifier: Modifier = Modifier
) {
    val barWidth = 3.dp
    val spacing = 2.dp
    val maxHeight = 32f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(spacing.toPx()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (0 until barCount).forEach { i ->
            val barProgress = if (isPlaying) {
                val phase = (i.toFloat() / barCount) * 2 * kotlin.math.PI
                val animPhase = waveAnimProgress * 2 * kotlin.math.PI
                (kotlin.math.sin(phase + animPhase) + 1) / 2 * 0.7f + 0.15f
            } else {
                // Static waveform based on hash for consistent look
                val hash = (i * 31 + 17) % 100
                0.15f + (hash / 100f) * 0.7f
            }

            val playedProgress = min(1f, progress * barCount / (i + 1))
            val barColor = if (playedProgress >= 1f) color else color.copy(alpha = 0.4f)

            val barHeight = (barProgress * maxHeight * (0.5f + playedProgress * 0.5f)).dp

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight)
                    .background(barColor, RoundedCornerShape(1.5.dp))
                    .graphicsLayer {
                        if (isPlaying && i < progress * barCount) {
                            // Pulse effect for played bars
                            val pulse = (waveAnimProgress * 2) % 1f
                            alpha = 1f - pulse * 0.3f
                        }
                    }
            )
        }
    }
}

@Composable
fun VoiceRecorder(
    isRecording: Boolean,
    duration: Int,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()
    val animProgress by animateFloatAsState(
        targetValue = if (isRecording) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "recorder_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Recording indicator with pulsing circles
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = 0.8f + animProgress * 0.4f
                        scaleY = 0.8f + animProgress * 0.4f
                    }
            ) {
                // Outer pulsing rings
                AnimatedVisibility(visible = isRecording) {
                    (0..2).forEach { i ->
                        val ringProgress by remember { mutableStateOf(0f) }
                        LaunchedEffect(i) {
                            while (isRecording) {
                                ringProgress = (ringProgress + 0.005f) % 1f
                                delay(16)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(120.dp + i * 40.dp)
                                .graphicsLayer {
                                    scaleX = 0.5f + ringProgress * 0.5f
                                    scaleY = 0.5f + ringProgress * 0.5f
                                    alpha = 1f - ringProgress
                                }
                                .background(
                                    Color(0xFFE53935).copy(alpha = 0.15f),
                                    androidx.compose.foundation.shape.CircleShape
                                )
                        )
                    }
                }

                // Main record button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFFE53935),
                            androidx.compose.foundation.shape.CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { if (isRecording) onStop() else onStart() }
                        )
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (dragAmount.y < -100) {
                                        haptic.trigger(HapticType.Heavy)
                                        onCancel()
                                    }
                                },
                                onDragEnd = { },
                                onDragCancel = { }
                            )
                        }
                        .graphicsLayer {
                            scaleX = if (isRecording) 1f else 0.9f
                            scaleY = if (isRecording) 1f else 0.9f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop recording" else "Start recording",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Duration and slide to cancel text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = isRecording) {
                    Text(
                        text = formatDuration(duration),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }

                AnimatedVisibility(visible = isRecording) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Slide",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Сдвиньте влево для отмены",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Slide",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Waveform preview while recording
            AnimatedVisibility(visible = isRecording) {
                RecordingWaveform(
                    modifier = Modifier
                        .width(280.dp)
                        .height(60.dp)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun RecordingWaveform(
    modifier: Modifier = Modifier
) {
    val barCount = 40
    val maxHeight = 40f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (0 until barCount).forEach { i ->
            val barProgress by remember { mutableStateOf(0.15f + (Math.random() * 0.7f)) }
            LaunchedEffect(i) {
                var currentProgress = barProgress
                while (true) {
                    currentProgress = 0.15f + (Math.random() * 0.7f)
                    delay(50 + (Math.random() * 100).toLong())
                }
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((barProgress * maxHeight).dp)
                    .background(Color(0xFFE53935), RoundedCornerShape(1.5.dp))
                    .graphicsLayer { alpha = 0.6f + barProgress * 0.4f }
            )
        }
    }
}

fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}