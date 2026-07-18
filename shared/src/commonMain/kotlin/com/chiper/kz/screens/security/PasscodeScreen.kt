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
import kotlinx.coroutines.delay

class PasscodeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<PasscodeViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        PasscodeScreenContent(
            state = state,
            mode = state.mode,
            onDigitClick = { viewModel.onDigitEntered(it) },
            onDeleteClick = { viewModel.onDelete() },
            onForgotClick = { viewModel.onForgotClick() },
            onBiometricClick = { viewModel.onBiometricAuth() },
            onClose = { navigator.pop() },
            haptic = haptic
        )
    }
}

class PasscodeViewModel(
    private val securityRepository: SecurityRepository
) : androidx.lifecycle.ViewModel() {

    var uiState by mutableStateOf(PasscodeUiState())
        private set

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
            uiState = uiState.copy(
                isLoading = false,
                enteredCode = "",
                isSuccess = true
            )
        } else {
            uiState = uiState.copy(
                isLoading = false,
                enteredCode = "",
                error = "Неверный код",
                failedAttempts = securityRepository.getFailedAttempts()
            )
            securityRepository.recordFailedAttempt()
        }
    }

    fun onForgotClick() {
        uiState = uiState.copy(mode = PasscodeMode.Forgot)
    }

    fun onBiometricAuth() {
        // Trigger biometric auth
    }
}

enum class PasscodeMode {
    Enter, Create, Confirm, Forgot, Biometric
}

data class PasscodeUiState(
    val mode: PasscodeMode = PasscodeMode.Enter,
    val enteredCode: String = "",
    val error: String? = null,
    val failedAttempts: Int = 0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@Composable
fun PasscodeScreenContent(
    state: PasscodeUiState,
    mode: PasscodeMode,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onForgotClick: () -> Unit,
    onBiometricClick: () -> Unit,
    onClose: () -> Unit,
    haptic: HapticFeedback
) {
    var code by remember { mutableStateOf(state.enteredCode) }
    
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

                    // Logo
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
                                        center = androidx.compose.ui.graphics.Offset(0.3f, 0.3f),
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
                        text = when (mode) {
                            PasscodeMode.Enter -> "Введите код доступа"
                            PasscodeMode.Create -> "Создайте код доступа"
                            PasscodeMode.Confirm -> "Подтвердите код доступа"
                            PasscodeMode.Forgot -> "Восстановление доступа"
                            PasscodeMode.Biometric -> "Биометрия"
                        },
                        style = GlassTypography.HeadlineSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (mode == PasscodeMode.Forgot) {
                        Text(
                            text = "Нажмите \"Забыли код\" для сброса через email",
                            style = GlassTypography.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        Text(
                            text = when (mode) {
                                PasscodeMode.Create -> "Используйте 4 цифры"
                                PasscodeMode.Confirm -> "Повторите код для подтверждения"
                                else -> ""
                            },
                            style = GlassTypography.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Code indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        (0..3).forEach { index ->
                            val filled = index < code.length
                            val error = state.error != null
                            
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        scaleX = if (filled) 1f else 0.8f
                                        scaleY = if (filled) 1f else 0.8f
                                    }
                                    .clip(CircleShape)
                                    .background(
                                        if (filled) {
                                            if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        CircleShape
                                    )
                                    .wrapContentSize(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                if (filled) {
                                    Text(
                                        text = "•",
                                        style = GlassTypography.HeadlineMedium,
                                        color = if (error) Color.White else Color.White
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = state.error != null) {
                        Text(
                            text = state.error!!,
                            style = GlassTypography.BodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Biometric button
                    if (mode == PasscodeMode.Enter) {
                        GlassIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Fingerprint",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            onClick = { 
                                haptic.trigger(HapticType.Light)
                                onBiometricClick() 
                            },
                            size = 64.dp,
                            variant = GlassButtonVariant.Secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Использовать отпечаток/лицо",
                            style = GlassTypography.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Forgot button
                    if (mode == PasscodeMode.Enter) {
                        TextButton(onClick = { haptic.trigger(HapticType.Selection); onForgotClick() }) {
                            Text(
                                text = "Забыли код?",
                                style = GlassTypography.LabelLarge.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Keypad
                    VirtualKeypad(
                        code = code,
                        onDigitClick = { d ->
                            haptic.trigger(HapticType.Selection)
                            onDigitClick(d)
                            if (code.length < 4) code += d
                        },
                        onDeleteClick = {
                            haptic.trigger(HapticType.Light)
                            onDeleteClick()
                            if (code.isNotEmpty()) code = code.dropLast(1)
                        },
                        onSubmit = {},
                        haptic = haptic
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun VirtualKeypad(
    code: String,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSubmit: () -> Unit,
    haptic: HapticFeedback
) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    if (key.isBlank()) {
                        Box(modifier = Modifier.size(68.dp))
                    } else if (key == "⌫") {
                        GlassIconButton(
                            icon = { Icon(Icons.Default.Backspace, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurface) },
                            onClick = { haptic.trigger(HapticType.Light); onDeleteClick() },
                            size = 68.dp
                        )
                    } else {
                        GlassKeyButton(
                            digit = key,
                            onClick = { haptic.trigger(HapticType.Selection); onDigitClick(key) }
                        )
                    }
                }
            }
        }
    }
}