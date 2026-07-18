package com.chiper.kz.screens.media

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

class VideoMessageScreen(
    val onRecordComplete: (String, Int) -> Unit, // video path, duration
    val onCancel: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<VideoMessageViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        VideoMessageScreenContent(
            state = state,
            onRecordStart = { viewModel.startRecording() },
            onRecordStop = { viewModel.stopRecording() },
            onSwitchCamera = { viewModel.switchCamera() },
            onFlashToggle = { viewModel.toggleFlash() },
            onCancel = { onCancel(); navigator.pop() },
            haptic = haptic
        )
    }
}

class VideoMessageViewModel : androidx.lifecycle.ViewModel() {
    var uiState by mutableStateOf(VideoMessageUiState())
        private set

    fun startRecording() {
        uiState = uiState.copy(isRecording = true, startTime = System.currentTimeMillis())
    }

    fun stopRecording() {
        val duration = (System.currentTimeMillis() - uiState.startTime) / 1000
        uiState = uiState.copy(
            isRecording = false,
            recordedVideo = "file:///storage/emulated/0/DCIM/video_${System.currentTimeMillis()}.mp4",
            videoDuration = duration.toInt()
        )
    }

    fun switchCamera() {
        uiState = uiState.copy(facingBack = !uiState.facingBack)
    }

    fun toggleFlash() {
        uiState = uiState.copy(flashMode = when (uiState.flashMode) {
            FlashMode.Off -> FlashMode.On
            FlashMode.On -> FlashMode.Auto
            else -> FlashMode.Off
        })
    }
}

enum class FlashMode { Off, On, Auto }

data class VideoMessageUiState(
    val facingBack: Boolean = true,
    val flashMode: FlashMode = FlashMode.Auto,
    val isRecording: Boolean = false,
    val startTime: Long = 0L,
    val recordedVideo: String? = null,
    val videoDuration: Int = 0,
    val zoomLevel: Float = 1f
)

@Composable
fun VideoMessageScreenContent(
    state: VideoMessageUiState,
    onRecordStart: () -> Unit,
    onRecordStop: () -> Unit,
    onSwitchCamera: () -> Unit,
    onFlashToggle: () -> Unit,
    onCancel: () -> Unit,
    haptic: HapticFeedback
) {
    val recordProgress by animateFloatAsState(
        targetValue = if (state.isRecording) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "record_progress"
    )

    ChiperTheme {
        GlassBackground(animated = true) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Camera preview
                GlassSurface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(0.dp),
                    elevation = GlassElevation.Level0
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video Recording",
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Video Recording",
                                style = GlassTypography.HeadlineSmall.copy(color = Color.White.copy(alpha = 0.5f))
                            )
                        }
                    }
                }

                // Top bar
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = GlassShapes.Card,
                    elevation = GlassElevation.Level1
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onCancel() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        GlassIconButton(
                            icon = {
                                when (state.flashMode) {
                                    FlashMode.Off -> Icon(Icons.Default.FlashOff, contentDescription = "Flash off", tint = Color.White, modifier = Modifier.size(24.dp))
                                    FlashMode.On -> Icon(Icons.Default.FlashOn, contentDescription = "Flash on", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                                    FlashMode.Auto -> Icon(Icons.Default.FlashAuto, contentDescription = "Flash auto", tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            },
                            onClick = { haptic.trigger(HapticType.Selection); onFlashToggle() },
                            size = 44.dp,
                            variant = GlassButtonVariant.Secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Duration
                if (state.isRecording) {
                    var formattedDuration by remember { mutableStateOf("00:00") }
                    androidx.compose.runtime.LaunchedEffect(state.isRecording) {
                        if (state.isRecording) {
                            androidx.lifecycle.viewmodel.viewModelScope.launch {
                                while (state.isRecording) {
                                    val elapsed = (System.currentTimeMillis() - state.startTime) / 1000
                                    formattedDuration = formatDuration(elapsed.toInt())
                                    kotlinx.coroutines.delay(1000)
                                }
                            }
                        }
                    }

                    Text(
                        text = formattedDuration,
                        style = GlassTypography.DisplaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    )
                }

                // Bottom controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Switch camera
                        GlassIconButton(
                            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Switch camera", tint = Color.White, modifier = Modifier.size(28.dp)) },
                            onClick = { haptic.trigger(HapticType.Selection); onSwitchCamera() },
                            size = 56.dp,
                            variant = GlassButtonVariant.Secondary
                        )

                        // Record button
                        val recordScale by animateFloatAsState(
                            targetValue = if (state.isRecording) 1.15f else 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "record_pulse"
                        )

                        GlassSurface(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(recordScale)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (state.isRecording) onRecordStop() else onRecordStart()
                                },
                            shape = CircleShape,
                            elevation = GlassElevation.Floating
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (state.isRecording)
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFFFF0000),
                                                    Color(0xFFCC0000)
                                                )
                                            )
                                            else
                                            Brush.radialGradient(
                                                center = Offset(0.3f, 0.3f),
                                                radius = 0.8f,
                                                colors = listOf(
                                                    Color.White,
                                                    Color(0xFFE0E0E0)
                                                )
                                            ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isRecording) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color.White, RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }

                        // Cancel/Delete
                        GlassIconButton(
                            icon = { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(28.dp)) },
                            onClick = { haptic.trigger(HapticType.Heavy); onCancel() },
                            size = 56.dp,
                            variant = GlassButtonVariant.Destructive
                        )
                    }
                }
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}