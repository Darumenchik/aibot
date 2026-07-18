package com.chiper.kz.screens.notifications

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class NotificationsSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<NotificationsSettingsViewModel>()
        val state = viewModel.uiState

        NotificationsSettingsContent(
            state = state,
            onNotificationToggle = { viewModel.setNotificationsEnabled(it) },
            onSoundClick = { viewModel.showSoundPicker() },
            onVibrationToggle = { viewModel.setVibrationEnabled(it) },
            onPriorityToggle = { viewModel.setPriorityEnabled(it) },
            onPerChatClick = { viewModel.showPerChatNotifications() },
            onDNDClick = { viewModel.showDNDSettings() },
            onClose = { navigator.pop() }
        )
    }
}

class NotificationsSettingsViewModel : androidx.lifecycle.ViewModel() {
    var uiState by mutableStateOf(NotificationsSettingsUiState())
        private set

    fun setNotificationsEnabled(enabled: Boolean) {
        uiState = uiState.copy(notificationsEnabled = enabled)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        uiState = uiState.copy(vibrationEnabled = enabled)
    }

    fun setPriorityEnabled(enabled: Boolean) {
        uiState = uiState.copy(priorityEnabled = enabled)
    }

    fun showSoundPicker() {
        uiState = uiState.copy(showSoundPicker = true)
    }

    fun showPerChatNotifications() {
        // Navigate
    }

    fun showDNDSettings() {
        // Navigate
    }
}

data class NotificationsSettingsUiState(
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val selectedSound: String = "Default",
    val vibrationEnabled: Boolean = true,
    val priorityEnabled: Boolean = true,
    val showSoundPicker: Boolean = false,
    val perChatNotifications: List<PerChatNotification> = listOf(
        PerChatNotification("Chat 1", true, "Default", true),
        PerChatNotification("Chat 2", false, "Custom", false),
        PerChatNotification("Chat 3", true, "Default", true)
    ),
    val dndEnabled: Boolean = false,
    val dndSchedule: String? = null
)

data class PerChatNotification(
    val name: String,
    val enabled: Boolean,
    val sound: String,
    val vibration: Boolean
)

@Composable
fun NotificationsSettingsContent(
    state: NotificationsSettingsUiState,
    onNotificationToggle: (Boolean) -> Unit,
    onSoundClick: () -> Unit,
    onVibrationToggle: (Boolean) -> Unit,
    onPriorityToggle: (Boolean) -> Unit,
    onPerChatClick: () -> Unit,
    onDNDClick: () -> Unit,
    onClose: () -> Unit
) {
    ChiperTheme {
        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Уведомления",
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
                ) {
                    // Global settings
                    GlassSettingsSection(
                        title = "Общие",
                        visible = true,
                        delayMs = 0
                    ) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Уведомления",
                            subtitle = if (state.notificationsEnabled) "Включены" else "Выключены",
                            trailing = {
                                androidx.compose.material3.Switch(
                                    checked = state.notificationsEnabled,
                                    onCheckedChange = onNotificationToggle,
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = TelegramBlue,
                                        checkedTrackColor = TelegramPaleBlue
                                    )
                                )
                            }
                        )

                        GlassSettingsItem(
                            icon = Icons.Outlined.NotificationsActive,
                            title = "Приоритетные уведомления",
                            subtitle = "Показывать на экране блокировки",
                            trailing = {
                                androidx.compose.material3.Switch(
                                    checked = state.priorityEnabled,
                                    onCheckedChange = onPriorityToggle,
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = TelegramBlue,
                                        checkedTrackColor = TelegramPaleBlue
                                    )
                                )
                            }
                        )
                    }

                    // Sound & vibration
                    GlassSettingsSection(
                        title = "Звук и вибрация",
                        visible = true,
                        delayMs = 100
                    ) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.VolumeUp,
                            title = "Звук уведомлений",
                            subtitle = state.selectedSound,
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = { onSoundClick() }
                        )

                        GlassSettingsItem(
                            icon = Icons.Outlined.Vibration,
                            title = "Вибрация",
                            subtitle = if (state.vibrationEnabled) "Включена" else "Выключена",
                            trailing = {
                                androidx.compose.material3.Switch(
                                    checked = state.vibrationEnabled,
                                    onCheckedChange = onVibrationToggle,
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = TelegramBlue,
                                        checkedTrackColor = TelegramPaleBlue
                                    )
                                )
                            }
                        )
                    }

                    // Per chat
                    GlassSettingsSection(
                        title = "Настройки чатов",
                        visible = true,
                        delayMs = 200
                    ) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.Chat,
                            title = "Уведомления для каждого чата",
                            subtitle = "Настроить индивидуально",
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = onPerChatClick
                        )
                    }

                    // DND
                    GlassSettingsSection(
                        title = "Не беспокоить",
                        visible = true,
                        delayMs = 300
                    ) {
                        GlassSettingsItem(
                            icon = Icons.Outlined.DoNotDisturb,
                            title = "Режим \"Не беспокоить\"",
                            subtitle = state.dndSchedule?.let { "Расписание: $it" } ?: "Выключен",
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = onDNDClick
                        )

                        GlassSettingsItem(
                            icon = Icons.Outlined.Schedule,
                            title = "Расписание",
                            subtitle = "Настроить автоматическое включение",
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = { /* Navigate to DND schedule */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun GlassSettingsSection(
    title: String,
    visible: Boolean,
    delayMs: Int,
    content: @Composable () -> Unit
) {
    var sectionVisible by remember { mutableStateOf(false) }
    val sectionAlpha by animateFloatAsState(
        targetValue = if (sectionVisible && visible) 1f else 0f,
        animationSpec = tween(300),
        label = "section_alpha"
    )
    val sectionOffset by animateDpAsState(
        targetValue = if (sectionVisible && visible) 0.dp else 30.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "section_offset"
    )

    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMs.toLong())
            sectionVisible = true
        }
    }

    Column(
        modifier = Modifier
            .alpha(sectionAlpha)
            .offset(y = sectionOffset)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        GlassSurface(
            modifier = Modifier
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
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    GlassSurface(
        modifier = Modifier
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
                tint = TelegramBlue,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            trailing?.invoke()
        }
    }
}

@Composable
fun SoundPickerDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    currentSound: String,
    onSoundSelected: (String) -> Unit
) {
    val sounds = listOf("Default", "Chime", "Glass", "Note", "Pop", "Tri-tone", "None")

    GlassModalBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Звук уведомления", style = GlassTypography.HeadlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Column {
                sounds.forEach { sound ->
                    val selected = sound == currentSound
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                onSoundSelected(sound)
                            }
                            .padding(16.dp),
                        shape = GlassShapes.Field,
                        elevation = if (selected) GlassElevation.Level1 else GlassElevation.Level0
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = sound, style = GlassTypography.BodyLarge)
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = TelegramBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}