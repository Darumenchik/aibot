package com.chiper.kz.screens.channels

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.glass.*
import com.chiper.kz.model.Channel
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay

class ChannelsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ChannelsViewModel>()
        val state = viewModel.uiState

        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Каналы",
                        leadingIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                            }
                        },
                        trailingIcons = listOf(
                            { IconButton(onClick = { viewModel.showSearch() }) {
                                Icon(Icons.Default.Search, contentDescription = "Поиск", tint = Color.White)
                            } }
                        )
                    )
                },
                floatingActionButton = {
                    GlassFAB(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Создать", tint = Color.White) },
                        onClick = { viewModel.showCreateChannelDialog() },
                        extendedText = "Новый канал"
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = GlassShapes.Card,
                            elevation = GlassElevation.Level2
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFFFBBC05).copy(alpha = 0.3f),
                                                    Color(0xFF5EB5F7).copy(alpha = 0.3f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBC05),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Создайте свой канал",
                                    style = GlassTypography.HeadlineSmall,
                                    color = TextPrimary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Вещайте для неограниченной аудитории.\nКаналы идеальны для публикаций и анонсов.",
                                    style = GlassTypography.BodyMedium,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                GlassButton(
                                    text = "Создать канал",
                                    onClick = { viewModel.showCreateChannelDialog() },
                                    variant = GlassButtonVariant.Primary,
                                    fullWidth = false,
                                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    items(
                        items = state.channels,
                        key = { it.id }
                    ) { channel ->
                        ChannelItem(
                            channel = channel,
                            onClick = {
                                navigator.push(com.chiper.kz.navigation.Screen.Channel(channel.id, channel.name, channel.avatarUrl))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelItem(
    channel: Channel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemVisible by remember { mutableStateOf(false) }
    val itemAlpha by animateFloatAsState(
        targetValue = if (itemVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "channel_item_alpha"
    )
    val itemOffset by animateDpAsState(
        targetValue = if (itemVisible) 0.dp else 30.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "channel_item_offset"
    )

    LaunchedEffect(channel.id) {
        delay(channel.id.hashCode().absoluteValue % 10 * 50L + 100)
        itemVisible = true
    }

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .alpha(itemAlpha)
            .offset(x = itemOffset)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(12.dp),
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
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFBBC05).copy(alpha = 0.4f),
                                Color(0xFF5EB5F7).copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Campaign,
                    contentDescription = null,
                    tint = Color(0xFFFBBC05),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = channel.name,
                            style = GlassTypography.TitleMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (channel.isVerified) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Верифицирован",
                                tint = TelegramBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    if (channel.unreadCount > 0) {
                        GlassChip(
                            text = channel.unreadCount.toString(),
                            variant = GlassChipVariant.Primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${channel.subscribersCount.formatSubscribers()} подписчиков",
                        style = GlassTypography.BodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "•",
                        style = GlassTypography.BodySmall,
                        color = TextSecondary.copy(alpha = 0.4f)
                    )
                    Text(
                        text = channel.lastPostTime?.let { formatTime(it) } ?: "Нет постов",
                        style = GlassTypography.BodySmall,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (channel.isMuted) {
                    Icon(
                        Icons.Default.NotificationsOff,
                        contentDescription = "Уведомления отключены",
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun Int.formatSubscribers(): String {
    return when {
        this >= 1000000 -> "${(this / 1000000.0).toString().take(3)}M подписчиков"
        this >= 1000 -> "${(this / 1000.0).toString().take(3)}K подписчиков"
        else -> "$this подписчиков"
    }
}