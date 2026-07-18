package com.chiper.kz.screens.security

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

class TwoFAScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<TwoFAViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        TwoFAScreenContent(
            state = state,
            onEnableClick = { viewModel.startSetup() },
            onDisableClick = { viewModel.disable2FA() },
            onCodeEntered = { viewModel.verifyCode(it) },
            onBackupCodeClick = { viewModel.showBackupCodes() },
            onBackupCodeEntered = { viewModel.verifyBackupCode(it) },
            onCopyCode = { viewModel.copyCode(it) },
            onClose = { navigator.pop() },
            haptic = haptic
        )
    }
}

class TwoFAViewModel(
    private val securityRepository: SecurityRepository
) : androidx.lifecycle.ViewModel() {

    var uiState by mutableStateOf(TwoFAUiState())
        private set

    fun startSetup() {
        // Generate TOTP secret
        val secret = generateSecret()
        val backupCodes = generateBackupCodes()
        uiState = uiState.copy(
            mode = TwoFAMode.Setup,
            secret = secret,
            backupCodes = backupCodes,
            qrCodeUrl = "otpauth://totp/Chiper:${secret}?secret=${secret}&issuer=Chiper"
        )
    }

    fun verifyCode(code: String) {
        if (code.length != 6) return
        
        if (uiState.mode == TwoFAMode.Setup) {
            // Verify and enable
            uiState = uiState.copy(isLoading = true)
            // In real app: verify TOTP
            securityRepository.setTotpEnabled(true, uiState.secret, uiState.backupCodes)
            uiState = uiState.copy(
                isLoading = false,
                mode = TwoFAMode.Enabled,
                secret = "",
                backupCodes = emptyList()
            )
        } else if (uiState.mode == TwoFAMode.Verify) {
            // Verify for sensitive action
            if (securityRepository.verifyTotp(code)) {
                uiState = uiState.copy(isSuccess = true)
            } else {
                uiState = uiState.copy(error = "Неверный код")
            }
        }
    }

    fun verifyBackupCode(code: String) {
        if (securityRepository.verifyBackupCode(code)) {
            uiState = uiState.copy(isSuccess = true)
        } else {
            uiState = uiState.copy(error = "Неверный резервный код")
        }
    }

    fun disable2FA() {
        uiState = uiState.copy(mode = TwoFAMode.DisableConfirm)
    }

    fun confirmDisable() {
        securityRepository.setTotpEnabled(false)
        uiState = uiState.copy(mode = TwoFAMode.Disabled)
    }

    fun showBackupCodes() {
        uiState = uiState.copy(mode = TwoFAMode.BackupCodes)
    }

    fun copyCode(code: String) {
        // Copy to clipboard
        uiState = uiState.copy(copiedCode = code)
    }

    private fun generateSecret(): String {
        // Generate base32 secret
        return "JBSWY3DPEHPK3PXP"
    }

    private fun generateBackupCodes(): List<String> {
        return (1..10).map { String.format("%08d", (Math.random() * 100000000).toInt()) }
    }
}

enum class TwoFAMode {
    Disabled, Setup, Enabled, Verify, DisableConfirm, BackupCodes
}

data class TwoFAUiState(
    val mode: TwoFAMode = TwoFAMode.Disabled,
    val secret: String = "",
    val backupCodes: List<String> = emptyList(),
    val qrCodeUrl: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val copiedCode: String? = null
)

@Composable
fun TwoFAScreenContent(
    state: TwoFAUiState,
    onEnableClick: () -> Unit,
    onDisableClick: () -> Unit,
    onCodeEntered: (String) -> Unit,
    onBackupCodeClick: () -> Unit,
    onBackupCodeEntered: (String) -> Unit,
    onCopyCode: (String) -> Unit,
    onClose: () -> Unit,
    haptic: HapticFeedback
) {
    ChiperTheme {
        GlassBackground(animated = true) {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Двухфакторная аутентификация",
                        leadingIcon = {
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state.mode) {
                        TwoFAMode.Disabled -> DisabledContent(onEnableClick = onEnableClick, haptic = haptic)
                        TwoFAMode.Setup -> SetupContent(
                            state = state,
                            onCodeEntered = onCodeEntered,
                            haptic = haptic
                        )
                        TwoFAMode.Enabled -> EnabledContent(
                            onDisableClick = onDisableClick,
                            onBackupCodeClick = onBackupCodeClick,
                            haptic = haptic
                        )
                        TwoFAMode.Verify -> VerifyContent(
                            onCodeEntered = onCodeEntered,
                            onBackupCodeEntered = onBackupCodeEntered,
                            haptic = haptic
                        )
                        TwoFAMode.DisableConfirm -> DisableConfirmContent(
                            onConfirm = { 
                                onDisableClick() 
                                // Actually disable
                            },
                            haptic = haptic
                        )
                        TwoFAMode.BackupCodes -> BackupCodesContent(
                            codes = state.backupCodes,
                            onCopy = onCopyCode,
                            haptic = haptic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisabledContent(onEnableClick: () -> Unit, haptic: HapticFeedback) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassSurface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            elevation = GlassElevation.Floating
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "2FA",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Двухфакторная аутентификация",
            style = GlassTypography.HeadlineMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Добавьте дополнительный слой защиты для вашего аккаунта",
            style = GlassTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        GlassButton(
            text = "Включить 2FA",
            onClick = {
                haptic.trigger(HapticType.Success)
                onEnableClick()
            },
            variant = GlassButtonVariant.Primary,
            fullWidth = false,
            leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
        )
    }
}

@Composable
fun SetupContent(
    state: TwoFAUiState,
    onCodeEntered: (String) -> Unit,
    haptic: HapticFeedback
) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = GlassShapes.Card,
            elevation = GlassElevation.Level2
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Настройка 2FA",
                    style = GlassTypography.HeadlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Отсканируйте QR-код в Google Authenticator или Authy",
                    style = GlassTypography.BodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // QR Code placeholder
                GlassSurface(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = GlassElevation.Level1
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Code",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "QR Code",
                            style = GlassTypography.TitleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Или введите секрет вручную:",
                    style = GlassTypography.LabelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = GlassShapes.Field,
                    elevation = GlassElevation.Level1
                ) {
                    Text(
                        text = state.secret,
                        style = GlassTypography.TitleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Введите код из приложения:",
                    style = GlassTypography.LabelLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                Text(
                                    text = code[index].toString(),
                                    style = GlassTypography.HeadlineMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Virtual keyboard
                VirtualKeypad(
                    code = code,
                    onDigitClick = { d ->
                        if (code.length < 6) {
                            code += d
                        }
                    },
                    onDeleteClick = { code = code.dropLast(1) },
                    onSubmit = { if (code.length == 6) onCodeEntered(code) },
                    haptic = haptic
                )
            }
        }
    }
}

@Composable
fun EnabledContent(
    onDisableClick: () -> Unit,
    onBackupCodeClick: () -> Unit,
    haptic: HapticFeedback
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassSurface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            elevation = GlassElevation.Floating
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "2FA Enabled",
                tint = MaterialTheme.colorScheme.success,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "2FA включена",
            style = GlassTypography.HeadlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ваш аккаунт защищен двухфакторной аутентификацией",
            style = GlassTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassButton(
                text = "Резервные коды",
                onClick = { haptic.trigger(HapticType.Selection); onBackupCodeClick() },
                variant = GlassButtonVariant.Secondary,
                fullWidth = false,
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp)) }
            )
            GlassButton(
                text = "Отключить",
                onClick = { haptic.trigger(HapticType.Medium); onDisableClick() },
                variant = GlassButtonVariant.Destructive,
                fullWidth = false,
                leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
            )
        }
    }
}

@Composable
fun VerifyContent(
    onCodeEntered: (String) -> Unit,
    onBackupCodeEntered: (String) -> Unit,
    haptic: HapticFeedback
) {
    var code by remember { mutableStateOf("") }
    var showBackupCodeInput by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Подтвердите вход",
            style = GlassTypography.HeadlineMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Введите код из Google Authenticator",
            style = GlassTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                if (code.length < 6) code += d
            },
            onDeleteClick = { code = code.dropLast(1) },
            onSubmit = { if (code.length == 6) onCodeEntered(code) },
            haptic = haptic
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { showBackupCodeInput = true }) {
            Text(
                text = "Использовать резервный код",
                style = GlassTypography.LabelLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun DisableConfirmContent(
    onConfirm: () -> Unit,
    haptic: HapticFeedback
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassSurface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            elevation = GlassElevation.Floating
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Отключить 2FA?",
            style = GlassTypography.HeadlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ваш аккаунт станет менее защищенным. Рекомендуем оставить 2FA включенной.",
            style = GlassTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassButton(
                text = "Отмена",
                onClick = {},
                variant = GlassButtonVariant.Secondary,
                fullWidth = false
            )
            GlassButton(
                text = "Отключить",
                onClick = { haptic.trigger(HapticType.Success); onConfirm() },
                variant = GlassButtonVariant.Destructive,
                fullWidth = false
            )
        }
    }
}

@Composable
fun BackupCodesContent(
    codes: List<String>,
    onCopy: (String) -> Unit,
    haptic: HapticFeedback
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Резервные коды",
            style = GlassTypography.HeadlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Сохраните эти коды в безопасном месте. Каждый код можно использовать один раз.",
            style = GlassTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            codes.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { code ->
                        GlassSurface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                                .clickable {
                                    haptic.trigger(HapticType.Selection)
                                    onCopy(code)
                                },
                            shape = GlassShapes.Card,
                            elevation = GlassElevation.Level1
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = code,
                                    style = GlassTypography.TitleMedium.copy(
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Копировать",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
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

@Composable
fun GlassKeyButton(
    digit: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier
            .size(68.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = CircleShape,
        elevation = GlassElevation.Level1
    ) {
        Text(
            text = digit,
            style = GlassTypography.HeadlineMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}