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

class CameraScreen(
    val onCapture: (String) -> Unit, // Returns file path
    val onCancel: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<CameraViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        CameraScreenContent(
            state = state,
            onCaptureClick = { viewModel.capturePhoto() },
            onSwitchCamera = { viewModel.switchCamera() },
            onFlashToggle = { viewModel.toggleFlash() },
            onGalleryClick = { viewModel.openGallery() },
            onCancelClick = { onCancel(); navigator.pop() },
            haptic = haptic
        )
    }
}

class CameraViewModel : androidx.lifecycle.ViewModel() {
    var uiState by mutableStateOf(CameraUiState())
        private set

    fun capturePhoto() {
        uiState = uiState.copy(isCapturing = true)
        // Capture photo logic
        uiState = uiState.copy(isCapturing = false, capturedPhoto = "file:///storage/emulated/0/DCIM/photo_${System.currentTimeMillis()}.jpg")
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

    fun openGallery() {
        // Open gallery picker
    }
}

enum class FlashMode { Off, On, Auto }

data class CameraUiState(
    val facingBack: Boolean = true,
    val flashMode: FlashMode = FlashMode.Auto,
    val isCapturing: Boolean = false,
    val capturedPhoto: String? = null,
    val zoomLevel: Float = 1f
)

@Composable
fun CameraScreenContent(
    state: CameraUiState,
    onCaptureClick: () -> Unit,
    onSwitchCamera: () -> Unit,
    onFlashToggle: () -> Unit,
    onGalleryClick: () -> Unit,
    onCancelClick: () -> Unit,
    haptic: HapticFeedback
) {
    ChiperTheme {
        GlassBackground(animated = true) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Camera preview placeholder
                GlassSurface(
                    modifier = Modifier.fillMaxSize(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
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
                                contentDescription = "Camera Preview",
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Camera Preview",
                                style = GlassTypography.HeadlineSmall.copy(color = Color.White.copy(alpha = 0.5f))
                            )
                        }
                    }
                }

                // Top controls
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
                        IconButton(onClick = onCancelClick) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Flash toggle
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

                // Bottom controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode selector
                    GlassSurface(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, bottom = 24.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Level1
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CameraModeItem(
                                label = "Фото",
                                selected = true,
                                onClick = {}
                            )
                            CameraModeItem(
                                label = "Видео",
                                selected = false,
                                onClick = {}
                            )
                            CameraModeItem(
                                label = "Портрет",
                                selected = false,
                                onClick = {}
                            )
                        }
                    }

                    // Capture button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery
                        GlassIconButton(
                            icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(28.dp)) },
                            onClick = { haptic.trigger(HapticType.Selection); onGalleryClick() },
                            size = 56.dp,
                            variant = GlassButtonVariant.Secondary
                        )

                        // Capture
                        val captureScale by animateFloatAsState(
                            targetValue = if (state.isCapturing) 0.8f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh)
                        )
                        GlassSurface(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(captureScale)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onCaptureClick() },
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
                                                Color.White,
                                                Color(0xFFE0E0E0)
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isCapturing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }

                        // Switch camera
                        GlassIconButton(
                            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Switch camera", tint = Color.White, modifier = Modifier.size(28.dp)) },
                            onClick = { haptic.trigger(HapticType.Selection); onSwitchCamera() },
                            size = 56.dp,
                            variant = GlassButtonVariant.Secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraModeItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
            style = GlassTypography.LabelLarge
        )
    }
}