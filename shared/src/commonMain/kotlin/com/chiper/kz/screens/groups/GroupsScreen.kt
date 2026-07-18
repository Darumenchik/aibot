package com.chiper.kz.screens.groups

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
import com.chiper.kz.model.Group
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay

class GroupsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<GroupsViewModel>()
        val state = viewModel.uiState

        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = "Группы",
                        leadingIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                            }
                        },
                        trailingIcons = listOf(
                            { IconButton(onClick = { viewModel.showCreateGroupDialog() }) {
                                Icon(Icons.Default.Add, contentDescription = "Создать группу", tint = Color.White)
                            } }
                        )
                    )
                },
                floatingActionButton = {
                    GlassFAB(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Создать", tint = Color.White) },
                        onClick = { viewModel.showCreateGroupDialog() },
                        extendedText = "Новая группа"
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
                                                    Color(0xFF2AABEE).copy(alpha = 0.3f),
                                                    Color(0xFF4DCD5E).copy(alpha = 0.3f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.People,
                                        contentDescription = null,
                                        tint = Color(0xFF2AABEE),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Создайте свою группу",
                                    style = GlassTypography.HeadlineSmall,
                                    color = TextPrimary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Добавьте до 200 000 участников,\nназначьте администраторов и управляйте настройками",
                                    style = GlassTypography.BodyMedium,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                GlassButton(
                                    text = "Создать группу",
                                    onClick = { viewModel.showCreateGroupDialog() },
                                    variant = GlassButtonVariant.Primary,
                                    fullWidth = false,
                                    leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    items(
                        items = state.groups,
                        key = { it.id }
                    ) { group ->
                        GroupItem(
                            group = group,
                            onClick = {
                                navigator.push(com.chiper.kz.navigation.Screen.GroupChat(group.id, group.name, group.avatarUrl))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupItem(
    group: Group,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemVisible by remember { mutableStateOf(false) }
    val itemAlpha by animateFloatAsState(
        targetValue = if (itemVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "group_item_alpha"
    )
    val itemOffset by animateDpAsState(
        targetValue = if (itemVisible) 0.dp else 30.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "group_item_offset"
    )

    LaunchedEffect(group.id) {
        delay(group.id.hashCode().absoluteValue % 10 * 50L + 100)
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
                                avatarColor(group.name).copy(alpha = 0.5f),
                                avatarColor(group.name).copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(group.name),
                    color = avatarColor(group.name),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.name,
                        style = GlassTypography.TitleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (group.unreadCount > 0) {
                        GlassChip(
                            text = group.unreadCount.toString(),
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
                        text = "${group.membersCount.formatSubscribers()} участников",
                        style = GlassTypography.BodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "•",
                        style = GlassTypography.BodySmall,
                        color = TextSecondary.copy(alpha = 0.4f)
                    )
                    Text(
                        text = group.lastMessage,
                        style = GlassTypography.BodySmall,
                        color = TextSecondary.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatTime(group.lastMessageTime),
                    style = GlassTypography.LabelSmall,
                    color = TextSecondary.copy(alpha = 0.6f)
                )
                if (group.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4DCD5E))
                    )
                }
            }
        }
    }
}

fun Int.formatSubscribers(): String {
    return when {
        this >= 1000000 -> "${this / 1000000}M"
        this >= 1000 -> "${this / 1000}K"
        else -> this.toString()
    }
}