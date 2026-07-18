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

class SecuritySettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SecuritySettingsViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        SecuritySettingsContent(
            state = state,
            onPasscodeClick = { navigator.push(PasscodeScreen(PasscodeMode.Change)) },
            onBiometricToggle = { viewModel.toggleBiometric(it) },
            onAutoLockClick = { viewModel.showAutoLockOptions() },
            on2faClick = { navigator.push(TwoFAScreen()) },
            onDevicesClick = { navigator.push(DeviceManagementScreen()) },
            onSessionClick = { viewModel.showSessions() },
            onLockNowClick = { viewModel.lockApp() },
            haptic = haptic
        )
    }
}

class SecuritySettingsViewModel(
    private val securityRepository: SecurityRepository
) : androidx.lifecycle.ViewModel() {

    var uiState by mutableStateOf(SecuritySettingsUiState())
        private set

    init {
        loadSettings()
    }

    private fun loadSettings() {
        uiState = uiState.copy(
            passcodeEnabled = securityRepository.passcodeEnabled.value,
            biometricEnabled = securityRepository.biometricEnabled.value,
            autoLockTimeout = securityRepository.autoLockTimeout.value,
            twoFAEnabled = securityRepository.totpEnabled.value
        )
    }

    fun toggleBiometric(enabled: Boolean) {
        securityRepository.setBiometricEnabled(enabled)
        uiState = uiState.copy(biometricEnabled = enabled)
    }

    fun showAutoLockOptions() {
        uiState = uiState.copy(showAutoLockDialog = true)
    }

    fun setAutoLockTimeout(timeout: Long) {
        securityRepository.setAutoLockTimeout(timeout)
        uiState = uiState.copy(autoLockTimeout = timeout, showAutoLockDialog = false)
    }

    fun showSessions() {
        // Navigate to sessions
    }

    fun lockApp() {
        securityRepository.lock()
    }
}

data class SecuritySettingsUiState(
    val passcodeEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoLockTimeout: Long = 300000L,
    val twoFAEnabled: Boolean = false,
    val showAutoLockDialog: Boolean = false
)

@Composable
fun SecuritySettingsContent(
    state: SecuritySettingsUiState,
    onPasscodeClick: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onAutoLockClick: () -> Unit,
    on2faClick: () -> Unit,
    onDevicesClick: () -> Unit,
    onSessionClick: () -> Unit,
    onLockNowClick: () -> Unit,
    haptic: HapticFeedback
) {
    ChiperTheme {
        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Безопасность и приватность",
                        leadingIcon = {
                            IconButton(onClick = { /* navigator.pop() */ }) {
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
                ) {
                    // Passcode section
                    SecuritySection(title = "Код доступа", haptic = haptic) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.Lock,
                            title = "Код доступа",
                            subtitle = if (state.passcodeEnabled) "Включен" else "Выключен",
                            trailing = {
                                if (state.passcodeEnabled) {
                                    GlassIconButton(
                                        icon = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) },
                                        onClick = { haptic.trigger(HapticType.Selection); onPasscodeClick() },
                                        size = 40.dp
                                    )
                                }
                            },
                            onClick = { if (state.passcodeEnabled) { haptic.trigger(HapticType.Selection); onPasscodeClick() } }
                        )
                    }

                    if (state.passcodeEnabled) {
                        SecuritySection(title = "Биометрия", haptic = haptic) {
                            GlassSettingsItem(
                                icon = Icons.Outlined.Fingerprint,
                                title = "Face ID / Touch ID",
                                subtitle = if (state.biometricEnabled) "Включено" else "Выключено",
                                trailing = {
                                    androidx.compose.material3.Switch(
                                        checked = state.biometricEnabled,
                                        onCheckedChange = { haptic.trigger(HapticType.Selection); onBiometricToggle(it) },
                                        colors = androidx.compose.material3.SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                },
                            onClick = { haptic.trigger(HapticType.Selection); onBiometricToggle(!state.biometricEnabled) }
                        )

                        GlassSettingsItem(
                            icon = Icons.Outlined.Timer,
                            title = "Автоблокировка",
                            subtitle = formatTimeout(state.autoLockTimeout),
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            },
                            onClick = { haptic.trigger(HapticType.Selection); onAutoLockClick() }
                        )

                        GlassSettingsItem(
                            icon = Icons.Outlined.LockClock,
                            title = "Блокировать сейчас",
                            subtitle = "Мгновенная блокировка приложения",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) },
                            onClick = { haptic.trigger(HapticType.Selection); onLockNowClick() }
                        )
                    }

                    // 2FA
                    SecuritySection(title = "Двухфакторная аутентификация", haptic = haptic) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.Security,
                            title = "2FA (Google Authenticator)",
                            subtitle = if (state.twoFAEnabled) "Включена" else "Выключена",
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            },
                            onClick = { haptic.trigger(HapticType.Selection); on2faClick() }
                        )
                    }

                    // Devices
                    SecuritySection(title = "Устройства", haptic = haptic) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.Devices,
                            title = "Управление устройствами",
                            subtitle = "Просмотр и отзыв доступа",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) },
                            onClick = { haptic.trigger(HapticType.Selection); onDevicesClick() }
                        )

                        GlassSettingsItem(
                            icon = Icons.Outlined.History,
                            title = "Активные сессии",
                            subtitle = "Просмотр входов в аккаунт",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) },
                            onClick = { haptic.trigger(HapticType.Selection); onSessionClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SecuritySection(
    title: String,
    haptic: HapticFeedback,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.03f))
                .padding(vertical = 4.dp),
            shape = GlassShapes.Card,
            elevation = GlassElevation.Level1
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun GlassSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (pressed) 0.7f else 1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level0
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.CenterVertically),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing?.invoke()
        }
    }
}

fun formatTimeout(timeout: Long): String {
    return when {
        timeout == 0L -> "Сразу"
        timeout < 60000 -> "${timeout / 1000} сек"
        timeout < 3600000 -> "${timeout / 60000} мин"
        timeout < 86400000 -> "${timeout / 3600000} ч"
        else -> "${timeout / 86400000} дн"
    }
}