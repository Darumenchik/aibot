package com.chiper.kz.screens.calls

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.glass.*
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType
import com.chiper.kz.utils.rememberHapticFeedback

data class IncomingCallScreen(
    val callerId: String,
    val callerName: String,
    val callerAvatar: String,
    val isVideo: Boolean,
    val onAccept: () -> Unit,
    val onDecline: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val haptic = rememberHapticFeedback()

        ChiperTheme {
            GlassBackground(animated = true) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    // Caller info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GlassSurface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            elevation = GlassElevation.Floating
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            center = Offset(0.3f, 0.3f),
                                            radius = 0.8f,
                                            colors = listOf(
                                                TelegramBlue.copy(alpha = 0.3f),
                                                TelegramBlue
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getInitials(callerName),
                                    color = Color.White,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = if (isVideo) "Видеозвонок от $callerName" : "Звонок от $callerName",
                            style = GlassTypography.HeadlineSmall,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Text(
                            text = "Входящий ${if (isVideo) "видео" else "голосовой"} вызов",
                            style = GlassTypography.BodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decline button
                        val declineDragOffset by remember { mutableStateOf(0f) }
                        val declineScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh)
                        )

                        GlassSurface(
                            modifier = Modifier
                                .size(72.dp)
                                .graphicsLayer { scaleX = declineScale; scaleY = declineScale }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            if (dragAmount.x < -50) {
                                                haptic.trigger(HapticType.Success)
                                                onDecline()
                                            }
                                        },
                                        onDragEnd = { },
                                        onDragCancel = { }
                                    )
                                },
                            shape = CircleShape,
                            elevation = GlassElevation.Floating
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFE53935),
                                                Color(0xFFC62828)
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "Decline",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // Accept button
                        val acceptScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh)
                        )

                        GlassSurface(
                            modifier = Modifier
                                .size(72.dp)
                                .graphicsLayer { scaleX = acceptScale; scaleY = acceptScale }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            if (dragAmount.x > 50) {
                                                haptic.trigger(HapticType.Success)
                                                onAccept()
                                            }
                                        },
                                        onDragEnd = { },
                                        onDragCancel = { }
                                    )
                                },
                            shape = CircleShape,
                            elevation = GlassElevation.Floating
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF4DCD5E),
                                                Color(0xFF3CC44D)
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                                    contentDescription = "Accept",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun OutgoingCallScreen(
    calleeId: String,
    calleeName: String,
    calleeAvatar: String,
    isVideo: Boolean,
    onCancel: () -> Unit
) {
    val haptic = rememberHapticFeedback()

    ChiperTheme {
        GlassBackground(animated = true) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GlassSurface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        elevation = GlassElevation.Floating
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        center = Offset(0.3f, 0.3f),
                                        radius = 0.8f,
                                        colors = listOf(
                                            TelegramBlue.copy(alpha = 0.3f),
                                            TelegramBlue
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getInitials(calleeName),
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = if (isVideo) "Видеозвонок $calleeName" : "Звонок $calleeName",
                        style = GlassTypography.HeadlineSmall,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Text(
                        text = if (isVideo) "Ожидаем ответа..." : "Звоним...",
                        style = GlassTypography.BodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                GlassSurface(
                    modifier = Modifier
                        .size(72.dp)
                        .padding(bottom = 80.dp),
                    shape = CircleShape,
                    elevation = GlassElevation.Floating
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFE53935),
                                        Color(0xFFC62828)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Cancel",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveCallScreen(
    callerName: String,
    callerAvatar: String,
    isVideo: Boolean,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isCameraOff: Boolean,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onFlipCamera: () -> Unit,
    onEndCall: () -> Unit
) {
    var callDuration by remember { mutableStateOf(0) }
    val haptic = rememberHapticFeedback()

    LaunchedEffect(Unit) {
        val job = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                callDuration++
            }
        }
        onDispose { job.cancel() }
    }

    ChiperTheme {
        GlassBackground(animated = true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Full screen video/avatar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0D1117),
                                    Color(0xFF161B22)
                                )
                            )
                        )
                ) {
                    if (!isVideo) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            GlassSurface(
                                modifier = Modifier.size(160.dp),
                                shape = CircleShape,
                                elevation = GlassElevation.Floating
                            ) {
                                Text(
                                    text = getInitials(callerName),
                                    color = Color.White,
                                    fontSize = 64.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = callerName,
                                style = GlassTypography.DisplaySmall.copy(color = Color.White)
                            )
                        }
                    }
                }

                // Call duration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp)
                        .wrapContentSize(Alignment.TopCenter)
                ) {
                    Text(
                        text = formatCallDuration(callDuration),
                        style = GlassTypography.HeadlineSmall.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }

                // Call controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CallControlButton(
                            icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            label = "Микрофон",
                            isActive = isMuted,
                            onClick = { haptic.trigger(HapticType.Selection); onMuteToggle() },
                            haptic = rememberHapticFeedback()
                        )
                        CallControlButton(
                            icon = if (isVideo && isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            label = isVideo ? "Камера" : "Видео",
                            isActive = isVideo && isCameraOff,
                            onClick = { haptic.trigger(HapticType.Selection); if (isVideo) onCameraToggle() else onFlipCamera() },
                            haptic = rememberHapticFeedback()
                        )
                        CallControlButton(
                            icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            label = "Динамик",
                            isActive = isSpeakerOn,
                            onClick = { haptic.trigger(HapticType.Selection); onSpeakerToggle() },
                            haptic = rememberHapticFeedback()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // End call button
                    GlassSurface(
                        modifier = Modifier
                            .size(72.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { haptic.trigger(HapticType.Heavy); onEndCall() },
                        shape = CircleShape,
                        elevation = GlassElevation.Floating
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFE53935),
                                            Color(0xFFC62828)
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End call",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Длительность: ${formatCallDuration(callDuration)}",
                        style = GlassTypography.LabelLarge.copy(color = Color.White.copy(alpha = 0.7f))
                    )
                }
            }
        }
    }
}

@Composable
fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    haptic: HapticFeedback
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.trigger(HapticType.Selection)
                onClick()
            }
            .wrapContentSize(Alignment.Center)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlassSurface(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer { scaleX = if (pressed) 0.9f else 1f; scaleY = if (pressed) 0.9f else 1f },
            shape = CircleShape,
            elevation = if (isActive) GlassElevation.Level3 else GlassElevation.Floating
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isActive)
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryVariant
                                )
                            )
                            else
                                Brush.radialGradient(
                                    center = Offset(0.3f, 0.3f),
                                    radius = 0.8f,
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.15f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color.White else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = label,
            style = GlassTypography.LabelSmall.copy(color = Color.White.copy(alpha = 0.8f)),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

fun formatCallDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    if (hours > 0) return "%02d:%02d:%02d".format(hours, minutes, secs)
    return "%02d:%02d".format(minutes, secs)
}