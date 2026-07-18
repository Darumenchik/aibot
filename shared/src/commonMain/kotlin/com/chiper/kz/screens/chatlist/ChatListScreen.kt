package com.chiper.kz.screens.chatlist

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.glass.*
import com.chiper.kz.model.Chat
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay

class ChatListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ChatListViewModel>()
        val chats by viewModel.chats.collectAsState()

        var selectedTab by remember { mutableIntStateOf(0) }
        var searchActive by remember { mutableStateOf(false) }
        var searchText by remember { mutableStateOf(TextFieldValue("")) }
        val searchFocusRequester = remember { FocusRequester() }
        var fabRotation by remember { mutableFloatStateOf(0f) }
        var itemsVisible by remember { mutableStateOf(false) }

        val fabScale by animateFloatAsState(
            targetValue = if (itemsVisible) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "fab_scale"
        )

        LaunchedEffect(Unit) {
            delay(100)
            itemsVisible = true
        }

        LaunchedEffect(searchActive) {
            if (searchActive) {
                delay(300)
                searchFocusRequester.requestFocus()
            }
        }

        val filteredChats = if (searchText.text.isBlank()) chats
        else chats.filter {
            it.name.contains(searchText.text, ignoreCase = true) ||
                    it.lastMessage.contains(searchText.text, ignoreCase = true)
        }

        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = if (searchActive) "" else "Chiper",
                        leadingIcon = if (searchActive) {
                            {
                                IconButton(onClick = { searchActive = false; searchText = TextFieldValue("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
                                }
                            }
                        } else {
                            {
                                IconButton(onClick = { navigator.push(com.chiper.kz.navigation.Screen.Groups) }) {
                                    Icon(Icons.Default.Groups, contentDescription = "Группы", tint = Color.White)
                                }
                            }
                        },
                        trailingIcons = listOf(
                            {
                                IconButton(onClick = { searchActive = !searchActive }) {
                                    Icon(
                                        if (searchActive) Icons.Default.Close else Icons.Default.Search,
                                        contentDescription = if (searchActive) "Закрыть" else "Поиск",
                                        tint = Color.White
                                    )
                                }
                            },
                            {
                                IconButton(onClick = { navigator.push(com.chiper.kz.navigation.Screen.Channels) }) {
                                    Icon(Icons.Default.Campaign, contentDescription = "Каналы", tint = Color.White)
                                }
                            }
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { fabRotation += 90f },
                        modifier = Modifier
                            .scale(fabScale)
                            .size(56.dp),
                        containerColor = TelegramBlue,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Новый чат",
                            modifier = Modifier.rotate(fabRotation)
                        )
                    }
                },
                containerColor = Color(0xFF0D1117)
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding)
                ) {
                    // Tab bar
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Level1
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            GlassTabItem(
                                text = "Чаты",
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 }
                            )
                            GlassTabItem(
                                text = "Группы",
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 }
                            )
                            GlassTabItem(
                                text = "Каналы",
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search bar
                    AnimatedVisibility(
                        visible = searchActive,
                        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                        exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                    ) {
                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = GlassShapes.Field,
                            elevation = GlassElevation.Level1
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                placeholder = { Text("Поиск", color = TextSecondary, style = GlassTypography.BodyMedium) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(searchFocusRequester)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = TelegramBlue,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Поиск",
                                        tint = Color.White
                                    )
                                }
                            )
                        }
                    }

                    // Chat list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(
                            items = filteredChats,
                            key = { it.id }
                        ) { chat ->
                            var itemVisible by remember { mutableStateOf(false) }
                            val itemAlpha by animateFloatAsState(
                                targetValue = if (itemVisible) 1f else 0f,
                                animationSpec = tween(300),
                                label = "item_alpha"
                            )
                            val itemOffsetX by animateFloatAsState(
                                targetValue = if (itemVisible) 0f else 100f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "item_offset"
                            )

                            LaunchedEffect(chat.id) {
                                delay(filteredChats.indexOf(chat) * 50L + 150)
                                itemVisible = true
                            }

                            Box(
                                modifier = Modifier
                                    .alpha(itemAlpha)
                                    .offset(x = itemOffsetX.dp)
                            ) {
                                ChatListItem(
                                    chat = chat,
                                    onClick = {
                                        viewModel.markAsRead(chat.id)
                                        navigator.push(
                                            com.chiper.kz.navigation.Screen.Chat(
                                                chatId = chat.id,
                                                chatName = chat.name,
                                                avatarUrl = chat.avatarUrl
                                            )
                                        )
                                    }
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
fun GlassTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "tab_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Color.White else TextSecondary,
            style = GlassTypography.LabelLarge
        )
    }
}