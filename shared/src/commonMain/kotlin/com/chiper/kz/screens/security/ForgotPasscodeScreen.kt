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

class ForgotPasscodeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ForgotPasscodeViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        ForgotPasscodeContent(
            state = state,
            onEmailChanged = { viewModel.onEmailChanged(it) },
            onCodeEntered = { viewModel.onCodeEntered(it) },
            onSubmit = { viewModel.onSubmit() },
            onResendClick = { viewModel.resendCode() },
            onCancelClick = { navigator.pop() },
            haptic = haptic
        )
    }
}

class ForgotPasscodeViewModel(
    private val securityRepository: SecurityRepository,
    private val authRepository: com.chiper.kz.data.AuthRepository
) : androidx.lifecycle.ViewModel() {

    var uiState by mutableStateOf(ForgotPasscodeUiState())
        private set

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(email = email, error = null)
    }

    fun onCodeEntered(code: String) {
        uiState = uiState.copy(enteredCode = code, error = null)
    }

    fun onSubmit() {
        if (uiState.isLoading) return
        if (uiState.email.isBlank()) {
            uiState = uiState.copy(error = "Введите email")
            return
        }
        if (uiState.step == 1 && uiState.email.isNotBlank()) {
            uiState = uiState.copy(isLoading = true)
            // Send reset code via email
            sendResetCode()
        } else if (uiState.step == 2) {
            if (uiState.enteredCode.length != 6) {
                uiState = uiState.copy(error = "Введите 6-значный код")
                return
            }
            uiState = uiState.copy(isLoading = true)
            verifyCode()
        }
    }

    fun resendCode() {
        if (uiState.isLoading || uiState.resendCooldown > 0) return
        sendResetCode()
    }

    private fun sendResetCode() {
        // In real app: send email with reset code
        uiState = uiState.copy(step = 2, isLoading = false, resendCooldown = 60)
        startCooldown()
    }

    private fun verifyCode() {
        // Verify code
        if (uiState.enteredCode == "123456") { // Demo code
            uiState = uiState.copy(step = 3, isLoading = false, enteredCode = "")
        } else {
            uiState = uiState.copy(isLoading = false, error = "Неверный код")
        }
    }

    private fun startCooldown() {
        androidx.lifecycle.viewmodel.viewModelScope.launch {
            while (uiState.resendCooldown > 0) {
                kotlinx.coroutines.delay(1000)
                uiState = uiState.copy(resendCooldown = uiState.resendCooldown - 1)
            }
        }
    }
}

data class ForgotPasscodeUiState(
    val step: Int = 1, // 1: email, 2: code, 3: new passcode
    val email: String = "",
    val enteredCode: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val resendCooldown: Int = 0
)

@Composable
fun ForgotPasscodeContent(
    state: ForgotPasscodeUiState,
    onEmailChanged: (String) -> Unit,
    onCodeEntered: (String) -> Unit,
    onSubmit: () -> Unit,
    onResendClick: () -> Unit,
    onCancelClick: () -> Unit,
    haptic: HapticFeedback
) {
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
                            Icon(
                                imageVector = Icons.Default.KeyOff,
                                contentDescription = "Forgot passcode",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = when (state.step) {
                            1 -> "Восстановление доступа"
                            2 -> "Введите код из письма"
                            3 -> "Новый код доступа"
                            else -> "Восстановление"
                        },
                        style = GlassTypography.HeadlineSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (state.step) {
                            1 -> "Введите email для получения кода восстановления"
                            2 -> "Мы отправили 6-значный код на ${state.email}"
                            3 -> "Создайте новый 4-значный код доступа"
                            else -> ""
                        },
                        style = GlassTypography.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    when (state.step) {
                        1 -> EmailStep(
                            email = state.email,
                            onEmailChanged = onEmailChanged,
                            onSubmit = { haptic.trigger(HapticType.Success); onSubmit() },
                            error = state.error,
                            isLoading = state.isLoading,
                            haptic = haptic
                        )
                        2 -> CodeStep(
                            code = state.enteredCode,
                            onCodeEntered = onCodeEntered,
                            onSubmit = { haptic.trigger(HapticType.Success); onSubmit() },
                            error = state.error,
                            isLoading = state.isLoading,
                            resendCooldown = state.resendCooldown,
                            onResendClick = { haptic.trigger(HapticType.Selection); onResendClick() },
                            haptic = haptic
                        )
                        3 -> NewPasscodeStep(
                            onSubmit = { haptic.trigger(HapticType.Success); onSubmit() },
                            haptic = haptic
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = { onCancelClick() }) {
                        Text(
                            text = "Отмена",
                            style = GlassTypography.LabelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun EmailStep(
    email: String,
    onEmailChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    error: String?,
    isLoading: Boolean,
    haptic: HapticFeedback
) {
    var email by remember { mutableStateOf(email) }

    GlassTextField(
        value = email,
        onValueChange = { email = it; onEmailChanged(it) },
        label = "Email",
        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.EmailAddress,
            imeAction = androidx.compose.ui.text.input.ImeAction.Done
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = onSubmit),
        isError = error != null,
        errorText = error
    )

    Spacer(modifier = Modifier.height(24.dp))

    GlassButton(
        text = if (isLoading) "Отправка..." else "Отправить код",
        onClick = onSubmit,
        fullWidth = true,
        loading = isLoading
    )
}

@Composable
fun CodeStep(
    code: String,
    onCodeEntered: (String) -> Unit,
    onSubmit: () -> Unit,
    error: String?,
    isLoading: Boolean,
    resendCooldown: Int,
    onResendClick: () -> Unit,
    haptic: HapticFeedback
) {
    var code by remember { mutableStateOf(code) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        (0..5).forEach { index ->
            val filled = index < code.length
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(
                        if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                if (filled) {
                    Text(text = code[index].toString(), style = GlassTypography.HeadlineMedium, color = Color.White)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    VirtualKeypad(
        code = code,
        onDigitClick = { d ->
            haptic.trigger(HapticType.Selection)
            if (code.length < 6) {
                code += d
                onCodeEntered(code)
            }
        },
        onDeleteClick = {
            haptic.trigger(HapticType.Light)
            if (code.isNotEmpty()) {
                code = code.dropLast(1)
                onCodeEntered(code)
            }
        },
        onSubmit = {},
        haptic = haptic
    )

    Spacer(modifier = Modifier.height(24.dp))

    AnimatedVisibility(visible = error != null) {
        Text(text = error!!, style = GlassTypography.BodyMedium, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (resendCooldown > 0) {
        Text(
            text = "Повторная отправка через ${resendCooldown} сек",
            style = GlassTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    } else {
        TextButton(onClick = onResendClick) {
            Text(text = "Не получили код? Отправить снова", style = GlassTypography.LabelLarge)
        }
    }
}

@Composable
fun NewPasscodeStep(
    onSubmit: () -> Unit,
    haptic: HapticFeedback
) {
    Text(
        text = "Создайте новый код",
        style = GlassTypography.BodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    VirtualKeypad(
        code = "",
        onDigitClick = {},
        onDeleteClick = {},
        onSubmit = onSubmit,
        haptic = haptic
    )
}