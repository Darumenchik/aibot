package com.chiper.kz.screens.settings

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.chiper.kz.ui.ChatDensity
import com.chiper.kz.ui.ChatDensitySelector
import com.chiper.kz.ui.FontScaleSelector
import com.chiper.kz.ui.HighContrastToggle
import com.chiper.kz.ui.LandscapeModeNotice
import com.chiper.kz.ui.RTLPreview

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SettingsViewModel>()
        val state = viewModel.uiState
        val themeViewModel = androidx.lifecycle.viewmodel.viewModel<com.chiper.kz.theme.ThemeViewModel>()

        ChiperTheme {
            GlassBackground {
                Scaffold(
                    topBar = {
                        GlassTopAppBar(
                            title = "Настройки",
                            leadingIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                                }
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
                    // Account section
                    SettingsSection(title = "Аккаунт", haptic = rememberHapticFeedback()) {
                        SettingsItem(
                            icon = Icons.Outlined.Person,
                            title = "Профиль",
                            subtitle = state.userName,
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { navigator.push(com.chiper.kz.navigation.Screen.Profile) }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Security,
                            title = "Безопасность и приватность",
                            subtitle = "Код доступа, 2FA, устройства",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { navigator.push(com.chiper.kz.screens.security.SecuritySettingsScreen()) }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Уведомления",
                            subtitle = "Звуки, вибрация, превью",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(NotificationsSettingsScreen()) */ }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chat settings
                    SettingsSection(title = "Чаты", haptic = rememberHapticFeedback()) {
                        SettingsItem(
                            icon = Icons.Outlined.Chat,
                            title = "Плотность сообщений",
                            subtitle = state.chatDensity.displayName,
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = { /* show density picker */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.FontDownload,
                            title = "Размер шрифта",
                            subtitle = "${(state.fontScale * 100).toInt()}%",
                            trailing = {
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = { /* show font scale picker */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Wallpaper,
                            title = "Обои чата",
                            subtitle = "Свой фон для каждого чата",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(WallpaperSettingsScreen()) */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Translate,
                            title = "Язык приложения",
                            subtitle = "Русский",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(LanguageSettingsScreen()) */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Appearance
                    SettingsSection(title = "Внешний вид", haptic = rememberHapticFeedback()) {
                        SettingsItem(
                            icon = Icons.Outlined.Palette,
                            title = "Тема",
                            subtitle = when (themeViewModel.themeMode.value) {
                                is com.chiper.kz.theme.ThemeMode.Light -> "Светлая"
                                is com.chiper.kz.theme.ThemeMode.Dark -> "Тёмная"
                                is com.chiper.kz.theme.ThemeMode.System -> "По системе"
                            },
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(ThemeSettingsScreen()) */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.ColorLens,
                            title = "Цветовая схема",
                            subtitle = state.currentTheme.name,
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(ColorSchemeSettingsScreen()) */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Contrast,
                            title = "Высокий контраст",
                            subtitle = if (state.highContrast) "Включен" else "Выключен",
                            trailing = {
                                Switch(
                                    checked = state.highContrast,
                                    onCheckedChange = { viewModel.setHighContrast(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            },
                            onClick = { viewModel.setHighContrast(!state.highContrast) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Data & Storage
                    SettingsSection(title = "Данные и память", haptic = rememberHapticFeedback()) {
                        SettingsItem(
                            icon = Icons.Outlined.Storage,
                            title = "Использование памяти",
                            subtitle = "2.4 ГБ из 64 ГБ",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(StorageUsageScreen()) */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Download,
                            title = "Автозагрузка медиа",
                            subtitle = "Wi-Fi и мобильная сеть",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(AutoDownloadSettingsScreen()) */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.DeleteForever,
                            title = "Очистить кэш",
                            subtitle = "Освободить место",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* show clear cache dialog */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Advanced
                    SettingsSection(title = "Дополнительно", haptic = rememberHapticFeedback()) {
                        SettingsItem(
                            icon = Icons.Outlined.Flag,
                            title = "Язык интерфейса",
                            subtitle = "Русский",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(LanguageSettingsScreen()) */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            title = "О приложении",
                            subtitle = "Версия 1.0.0",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* show about dialog */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Help,
                            title = "Помощь и поддержка",
                            subtitle = "FAQ, обратная связь",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigator.push(HelpScreen()) */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Experimental features
                    SettingsSection(title = "Экспериментальные функции", haptic = rememberHapticFeedback()) {
                        SettingsItem(
                            icon = Icons.Outlined.ScreenRotation,
                            title = "Альбомный режим чатов",
                            subtitle = "Оптимизация для горизонтальной ориентации",
                            trailing = {
                                Switch(
                                    checked = state.landscapeMode,
                                    onCheckedChange = { viewModel.setLandscapeMode(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            },
                            onClick = { viewModel.setLandscapeMode(!state.landscapeMode) }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.FormatAlignJustify,
                            title = "RTL поддержка",
                            subtitle = "Для арабского/иврита",
                            trailing = {
                                Switch(
                                    checked = state.rtlMode,
                                    onCheckedChange = { viewModel.setRTLMode(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            },
                            onClick = { viewModel.setRTLMode(!state.rtlMode) }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
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
            color = TextSecondary,
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
fun SettingsItem(
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
fun SettingsItem(
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