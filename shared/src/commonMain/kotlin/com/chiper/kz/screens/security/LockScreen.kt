package com.chiper.kz.screens.security

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
import com.chiper.kz.security.SecurityRepository
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType
import com.chiper.kz.utils.rememberHapticFeedback

class LockScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<LockViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        LockScreenContent(
            state = state,
            onDigitClick = { viewModel.onDigitEntered(it) },
            onDeleteClick = { viewModel.onDelete() },
            onForgotClick = { viewModel.onForgotClick() },
            onBiometricClick = { viewModel.onBiometricAuth() },
            onUnlock = { navigator.pop() },
            haptic = haptic
        )
    }
}

class LockViewModel(
    private val securityRepository: SecurityRepository
) : androidx.lifecycle.ViewModel() {

    var uiState by mutableStateOf(LockUiState())
        private set

    init {
        checkLock()
    }

    private fun checkLock() {
        uiState = uiState.copy(isLocked = securityRepository.shouldAutoLock())
    }

    fun onDigitEntered(digit: String) {
        if (uiState.isLoading) return
        val newCode = uiState.enteredCode + digit
        uiState = uiState.copy(enteredCode = newCode)
        
        if (newCode.length == 4) {
            verifyPasscode(newCode)
        }
    }

    fun onDelete() {
        if (uiState.enteredCode.isNotEmpty()) {
            uiState = uiState.copy(enteredCode = uiState.enteredCode.dropLast(1))
        }
    }

    private fun verifyPasscode(code: String) {
        uiState = uiState.copy(isLoading = true)
        val success = securityRepository.verifyPasscode(code)
        if (success) {
            uiState = uiState.copy(isLoading = false, enteredCode = "", isSuccess = true)
        } else {
            uiState = uiState.copy(isLoading = false, enteredCode = "", error = "Неверный код")
        }
    }

    fun onForgotClick() {
        // Navigate to reset
    }
}

data class LockUiState(
    val enteredCode: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isLocked: Boolean = true
)

@Composable
fun LockScreenContent(
    state: LockUiState,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onForgotClick: () -> Unit,
    onBiometricClick: () -> Unit,
    onUnlock: () -> Unit,
    haptic: HapticFeedback
) {
    var code by remember { mutableStateOf("") }
    
    ChiperTheme {
        GlassBackground(animated = true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    // App logo with glass
                    GlassSurface(
                        modifier = Modifier.size(80.dp),
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
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.primary
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "C",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Chiper",
                        style = GlassTypography.DisplaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Приложение заблокировано",
                        style = GlassTypography.BodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Code indicators
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        (0..3).forEach { index ->
                            val filled = index < code.length
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (filled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                                    .wrapContentSize(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index < code.length) {
                                    Text(text = "•", style = GlassTypography.HeadlineMedium, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Biometric button
                    GlassIconButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {},
                        size = 64.dp,
                        variant = GlassButtonVariant.Secondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Использовать биометрию",
                        style = GlassTypography.BodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = {}) {
                        Text(
                            text = "Забыли код?",
                            style = GlassTypography.LabelLarge.copy(color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    VirtualKeypad(
                        code = "",
                        onDigitClick = { d ->
                            // haptic trigger
                        },
                        onDeleteClick = {},
                        onSubmit = {},
                        haptic = rememberHapticFeedback()
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}