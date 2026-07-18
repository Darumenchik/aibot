package com.chiper.kz.screens.profile

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.glass.*
import com.chiper.kz.components.avatarColor
import com.chiper.kz.components.getInitials
import com.chiper.kz.model.User
import com.chiper.kz.screens.settings.SettingsScreen
import com.chiper.kz.screens.security.SecuritySettingsScreen
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType
import com.chiper.kz.utils.rememberHapticFeedback
import kotlinx.coroutines.delay

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ProfileViewModel>()
        val user = viewModel.currentUser
        val haptic = rememberHapticFeedback()

        var itemsVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(100)
            itemsVisible = true
        }

        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Профиль",
                        elevation = GlassElevation.Level2
                    )
                },
                containerColor = Color(0xFF0D1117)
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Avatar section
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Floating
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            center = Offset(0.3f, 0.3f),
                                            radius = 0.8f,
                                            colors = listOf(
                                                avatarColor(user?.name ?: "U").copy(alpha = 0.5f),
                                                avatarColor(user?.name ?: "U").copy(alpha = 0.2f),
                                                avatarColor(user?.name ?: "U")
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getInitials(user?.name ?: "User"),
                                    color = Color.White,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = user?.name ?: "Пользователь",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = user?.email ?: "",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            if (user?.bio?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(12.dp))
                                GlassSurface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    shape = GlassShapes.Field,
                                    elevation = GlassElevation.Level0
                                ) {
                                    Text(
                                        text = user.bio,
                                        fontSize = 14.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Settings sections
                    SettingsSection(
                        title = "Аккаунт",
                        visible = itemsVisible,
                        delayMs = 0
                    ) {
                        SettingsItem(
                            icon = Icons.Outlined.Phone,
                            title = "Телефон",
                            subtitle = "+7 (777) 123-45-67"
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Email,
                            title = "Email",
                            subtitle = user?.email ?: ""
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            title = "О себе",
                            subtitle = user?.bio?.ifBlank { "Не указано" } ?: "Не указано"
                        )
                    }

                    SettingsSection(
                        title = "Настройки",
                        visible = itemsVisible,
                        delayMs = 100
                    ) {
                        SettingsItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Уведомления",
                            subtitle = "Включены",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { /* navigate */ }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Lock,
                            title = "Конфиденциальность",
                            subtitle = "",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Palette,
                            title = "Оформление",
                            subtitle = "Светлая тема",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Language,
                            title = "Язык",
                            subtitle = "Русский",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                    }

                    SettingsSection(
                        title = "Безопасность",
                        visible = itemsVisible,
                        delayMs = 200
                    ) {
                        SettingsItem(
                            icon = Icons.Outlined.Security,
                            title = "Безопасность и приватность",
                            subtitle = "Код доступа, 2FA, устройства",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                    }

                    SettingsSection(
                        title = "Данные и память",
                        visible = itemsVisible,
                        delayMs = 300
                    ) {
                        SettingsItem(
                            icon = Icons.Outlined.Storage,
                            title = "Использование памяти",
                            subtitle = "2.4 ГБ из 64 ГБ",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Download,
                            title = "Автозагрузка медиа",
                            subtitle = "Wi-Fi и мобильная сеть",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                    }

                    SettingsSection(
                        title = "Поддержка",
                        visible = itemsVisible,
                        delayMs = 400
                    ) {
                        SettingsItem(
                            icon = Icons.AutoMirrored.Outlined.HelpOutline,
                            title = "Помощь",
                            subtitle = "",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            title = "О приложении",
                            subtitle = "Версия 1.0.0",
                            trailing = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = { }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout button
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.logout() }
                            .padding(16.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Level2
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Выйти",
                                tint = ErrorRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Выйти",
                                fontSize = 16.sp,
                                color = ErrorRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
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
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
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
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressedAlpha by animateFloatAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 0.6f else 1f,
        animationSpec = tween(100),
        label = "settings_pressed"
    )

    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(pressedAlpha)
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