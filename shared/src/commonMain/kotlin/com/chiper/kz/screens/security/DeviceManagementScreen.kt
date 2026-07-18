package com.chiper.kz.screens.security

import androidx.compose.animation.animateDpAsState
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

class DeviceManagementScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<DeviceManagementViewModel>()
        val state = viewModel.uiState
        val haptic = rememberHapticFeedback()

        DeviceManagementContent(
            state = state,
            onRevokeDevice = { viewModel.revokeDevice(it) },
            onRevokeAllOthers = { viewModel.revokeAllOthers() },
            onRenameDevice = { viewModel.renameDevice(it) },
            onClose = { navigator.pop() },
            haptic = haptic
        )
    }
}

class DeviceManagementViewModel(
    private val securityRepository: SecurityRepository
) : androidx.lifecycle.ViewModel() {

    var uiState by mutableStateOf(DeviceManagementUiState())
        private set

    init {
        loadDevices()
    }

    private fun loadDevices() {
        uiState = uiState.copy(isLoading = true)
        // In real app: observe securityRepository.trustedDevices
        uiState = uiState.copy(
            devices = listOf(
                TrustedDevice("current", "Pixel 8 Pro", "Android", System.currentTimeMillis(), true),
                TrustedDevice("device2", "MacBook Pro", "macOS", System.currentTimeMillis() - 86400000, false),
                TrustedDevice("device3", "iPhone 15", "iOS", System.currentTimeMillis() - 172800000, false)
            ),
            isLoading = false
        )
    }

    fun revokeDevice(deviceId: String) {
        if (deviceId == uiState.devices.find { it.isCurrent }?.id) return
        securityRepository.removeTrustedDevice(deviceId)
        uiState = uiState.copy(devices = uiState.devices.filter { it.id != deviceId })
    }

    fun revokeAllOthers() {
        val currentId = uiState.devices.find { it.isCurrent }?.id
        securityRepository.revokeAllOtherDevices(currentId ?: "")
        uiState = uiState.copy(devices = uiState.devices.filter { it.isCurrent })
    }

    fun renameDevice(device: TrustedDevice) {
        // Show rename dialog
    }
}

data class DeviceManagementUiState(
    val devices: List<TrustedDevice> = emptyList(),
    val isLoading: Boolean = false
)

data class TrustedDevice(
    val id: String,
    val name: String,
    val platform: String,
    val lastActive: Long,
    val isCurrent: Boolean
)

@Composable
fun DeviceManagementContent(
    state: DeviceManagementUiState,
    onRevokeDevice: (String) -> Unit,
    onRevokeAllOthers: () -> Unit,
    onRenameDevice: (TrustedDevice) -> Unit,
    onClose: () -> Unit,
    haptic: HapticFeedback
) {
    ChiperTheme {
        GlassBackground(animated = true) {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Управление устройствами",
                        leadingIcon = {
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                            }
                        },
                        trailingIcons = listOf(
                            {
                                IconButton(onClick = { 
                                    haptic.trigger(HapticType.Warning)
                                    // Show revoke all dialog
                                }) {
                                    Icon(Icons.Default.Logout, contentDescription = "Выйти со всех", tint = Color.White)
                                }
                            }
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Current device section
                    if (state.devices.isNotEmpty()) {
                        state.devices.find { it.isCurrent }?.let { current ->
                            GlassSurface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = GlassShapes.Card,
                                elevation = GlassElevation.Level2
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            listOf(
                                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                                            )
                                                        ),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PhoneAndroid,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = current.name,
                                                    style = GlassTypography.TitleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Текущее устройство • ${current.platform}",
                                                    style = GlassTypography.BodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Активен сейчас",
                                            style = GlassTypography.LabelSmall.copy(color = MaterialTheme.colorScheme.success)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Other devices
                val otherDevices = state.devices.filter { !it.isCurrent }
                if (otherDevices.isNotEmpty()) {
                    Text(
                        text = "Другие устройства (${otherDevices.size})",
                        style = GlassTypography.LabelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(otherDevices) { device ->
                            DeviceItem(
                                device = device,
                                onRevoke = { onRevokeDevice(device.id) },
                                onRename = { onRenameDevice(device) },
                                haptic = rememberHapticFeedback()
                            )
                        }
                    }
                } else {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Level1
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Нет других устройств",
                                style = GlassTypography.TitleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Когда вы войдете на новом устройстве, оно появится здесь",
                                style = GlassTypography.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security actions
                if (otherDevices.isNotEmpty()) {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Level1
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Безопасность",
                                style = GlassTypography.LabelLarge
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            GlassButton(
                                text = "Выйти со всех других устройств",
                                onClick = {
                                    haptic.trigger(HapticType.Warning)
                                    onRevokeAllOthers()
                                },
                                variant = GlassButtonVariant.Destructive,
                                fullWidth = true,
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: TrustedDevice,
    onRevoke: () -> Unit,
    onRename: () -> Unit,
    haptic: HapticFeedback
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
    )

    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (device.platform.lowercase()) {
                    "android" -> Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    "ios" -> Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    "macos" -> Icon(Icons.Default.Computer, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    "windows" -> Icon(Icons.Default.DesktopMac, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    else -> Icon(Icons.Default.Devices, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = GlassTypography.TitleMedium
                )
                Text(
                    text = "${device.platform} • Активен ${formatRelativeTime(device.lastActive)}",
                    style = GlassTypography.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { haptic.trigger(HapticType.Selection); onRename() }) {
                    Icon(Icons.Default.Edit, contentDescription = "Переименовать", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(
                    onClick = {
                        haptic.trigger(HapticType.Heavy)
                        onRevoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Отозвать доступ",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Подробнее",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
            exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy))
        ) {
            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.Transparent),
                shape = GlassShapes.Card,
                elevation = GlassElevation.Level0
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoRow(label = "ID устройства", value = device.id.take(12) + "...")
                        InfoRow(label = "Последняя активность", value = formatFullTime(device.lastActive))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = GlassTypography.LabelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = GlassTypography.BodyMedium.copy(fontFamily = FontFamily.Monospace))
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "только что"
        diff < 3600000 -> "${diff / 60000} мин назад"
        diff < 86400000 -> "${diff / 3600000} ч назад"
        else -> "${diff / 86400000} дн назад"
    }
}

fun formatFullTime(timestamp: Long): String {
    return java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
}