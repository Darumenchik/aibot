package com.chiper.kz.screens.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.chat.*
import com.chiper.kz.components.glass.*
import com.chiper.kz.model.Message
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

data class ChatScreen(
    val chatId: String,
    val chatName: String,
    val avatarUrl: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ChatViewModel> { parametersOf(chatId) }
        val messages by viewModel.messages.collectAsState()
        val chatMessages = messages.filter { it.chatId == chatId }.sortedBy { it.timestamp }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.markAsRead()
        }

        GlassBackground {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = chatName,
                        subtitle = if (viewModel.isTyping) "печатает..." else "в сети",
                        leadingIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                            }
                        },
                        trailingIcons = listOf(
                            { IconButton(onClick = { }) {
                                Icon(Icons.Default.Call, contentDescription = "Звонок", tint = Color.White)
                            } },
                            { IconButton(onClick = { }) {
                                Icon(Icons.Default.Videocam, contentDescription = "Видеозвонок", tint = Color.White)
                            } },
                            { IconButton(onClick = { }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Ещё", tint = Color.White)
                            } }
                        )
                    )
                },
                containerColor = ChatBackground
            ) { padding ->
                PullToRefresh(
                    isRefreshing = false,
                    onRefresh = { /* viewModel.refresh() */ }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        // Messages
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            state = listState,
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = chatMessages,
                                key = { it.id }
                            ) { message ->
                                var visible by remember { mutableStateOf(false) }
                                val itemAlpha by animateFloatAsState(
                                    targetValue = if (visible) 1f else 0f,
                                    animationSpec = tween(250),
                                    label = "msg_alpha"
                                )
                                val itemOffsetY by animateDpAsState(
                                    targetValue = if (visible) 0.dp else 20.dp,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                                    label = "msg_offset"
                                )

                                LaunchedEffect(message.id) {
                                    delay(chatMessages.indexOf(message) * 30L)
                                    visible = true
                                }

                                Box(
                                    modifier = Modifier
                                        .alpha(itemAlpha)
                                        .offset(y = itemOffsetY)
                                ) {
                                    when (message.type) {
                                        MessageType.VOICE -> {
                                            VoiceMessageBubble(
                                                message = message,
                                                isPlaying = viewModel.playingMessageId == message.id,
                                                currentPosition = if (viewModel.playingMessageId == message.id) viewModel.playbackProgress else 0f,
                                                onPlayClick = { viewModel.toggleVoicePlay(message) },
                                                onLongPress = { viewModel.showMessageMenu(message) }
                                            )
                                        }
                                        MessageType.IMAGE -> {
                                            GlassMessageBubble(
                                                message = message,
                                                onLongPress = { viewModel.showMessageMenu(message) }
                                            )
                                        }
                                        else -> {
                                            GlassMessageBubble(
                                                message = message,
                                                onLongPress = { viewModel.showMessageMenu(message) }
                                            )
                                        }
                                    }
                                }
                            }

                            // Typing indicator
                            if (viewModel.isTyping) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, top = 8.dp),
                                    ) {
                                        GlassSurface(
                                            modifier = Modifier.padding(start = 8.dp, end = 64.dp),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 16.dp,
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            ),
                                            elevation = GlassElevation.Level1
                                        ) {
                                            TypingIndicator(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Input bar
                        GlassChatInputBar(
                            value = viewModel.inputText,
                            onValueChange = { viewModel.onInputChanged(it) },
                            onSend = {
                                viewModel.sendMessage()
                                coroutineScope.launch {
                                    delay(100)
                                    if (chatMessages.isNotEmpty()) {
                                        listState.animateScrollToItem(chatMessages.size)
                                    }
                                }
                            },
                            onVoiceRecordStart = { viewModel.startVoiceRecording() },
                            onVoiceRecordStop = { viewModel.stopVoiceRecording() },
                            onAttachClick = { viewModel.showAttachMenu() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassMessageBubble(
    message: Message,
    onReply: ((Message) -> Unit)? = null,
    onLongPress: (() -> Unit)? = nil,
    onReactionClick: ((Message, String) -> Unit)? = nil,
    onReactionAdd: ((Message, String) -> Unit)? = nil,
    isSelected: Boolean = false,
    showTime: Boolean = true,
    showStatus: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isSent = message.isSentByMe
    val haptic = HapticFeedback.rememberHaptic()
    var offsetX by remember { mutableStateOf(0f) }
    var isSwipeOpen by remember { mutableStateOf(false) }
    var showReactions by remember { mutableStateOf(false) }

    val bubbleColor = if (isSent) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isSent) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val maxSwipeDistance = 120f
    val swipeThreshold = 60f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isSent) 64.dp else 8.dp,
                end = if (isSent) 8.dp else 64.dp,
                top = 2.dp,
                bottom = 2.dp
            )
            .pointerInput(Unit) {
                if (isSent && !isSwipeOpen) {
                    detectDragGestures(
                        onDragStart = { },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = (offsetX + dragAmount.x).coerceIn(-maxSwipeDistance, 0f)
                            offsetX = newOffset
                            isSwipeOpen = newOffset < -swipeThreshold
                        },
                        onDragEnd = {
                            if (isSwipeOpen) {
                                // Keep open
                            } else {
                                offsetX = 0f
                                isSwipeOpen = false
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                            isSwipeOpen = false
                        }
                    )
                }
            }
            .offset(x = offsetX.dp)
    ) {
        // Swipe indicator
        if (isSent && !isSwipeOpen) {
            AnimatedVisibility(visible = offsetX < -20) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            GlassShapes.Circle
                        )
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Reply",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Swipe action area
        if (isSwipeOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        GlassShapes.Card
                    )
                    .clickable {
                        offsetX = 0f
                        isSwipeOpen = false
                        haptic.trigger(HapticType.Success)
                        onReply?.invoke(message)
                    }
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Reply",
                        tint = Color.White
                    )
                    Text(
                        text = "Ответить",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Message bubble
        GlassSurface(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = if (isSent) 20.dp else 4.dp,
                    topEnd = if (isSent) 4.dp else 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )),
            shape = RoundedCornerShape(
                topStart = if (isSent) 20.dp else 4.dp,
                topEnd = if (isSent) 4.dp else 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            elevation = GlassElevation.Level1
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Reply preview
                message.replyTo?.let { replyId ->
                    // Show reply preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), GlassShapes.Small)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Ответ на сообщение",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Message content with markdown
                MarkdownMessage(
                    text = message.text,
                    color = textColor,
                    fontSize = 15,
                    lineHeight = 20
                )

                // Reactions
                if (message.reactions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    MessageReactions(
                        reactions = message.reactions,
                        onReactionClick = onReactionClick,
                        onReactionAdd = onReactionAdd,
                        message = message,
                        isSent = isSent
                    )
                }

                // Time and status
                if (showTime || showStatus) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showTime) {
                            Text(
                                text = formatMessageTime(message.timestamp),
                                fontSize = 11.sp,
                                color = textColor.copy(alpha = 0.6f)
                            )
                        }
                        if (showStatus && isSent) {
                            Icon(
                                imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = if (message.isRead) "Read" else "Sent",
                                tint = if (message.isRead) MaterialTheme.colorScheme.primary else textColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Reactions panel
        AnimatedVisibility(
            visible = showReactions,
            enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(animationSpec = tween(150)),
            exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)) + fadeOut(animationSpec = tween(100))
        ) {
            ReactionPicker(
                message = message,
                onReactionSelected = { emoji ->
                    showReactions = false
                    haptic.trigger(HapticType.Selection)
                    onReactionAdd?.invoke(message, emoji)
                },
                onDismiss = { showReactions = false },
                isSent = isSent
            )
        }
    }
}

@Composable
fun GlassChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceRecordStart: (() -> Unit)? = null,
    onVoiceRecordStop: (() -> Unit)? = null,
    onAttachClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val hasText = value.isNotBlank()
    var isRecording by remember { mutableStateOf(false) }
    var recordScale by remember { mutableStateOf(1f) }

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level3
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attach button
            IconButton(onClick = { onAttachClick?.invoke() }) {
                Icon(
                    Icons.Default.AddCircleOutline,
                    contentDescription = "Прикрепить",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Text input
            GlassSurface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = GlassElevation.Level0
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text("Сообщение", color = MaterialTheme.colorScheme.onSurfaceVariant, style = GlassTypography.BodyMedium)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.textFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 4,
                    singleLine = false
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Voice record button
            if (!hasText) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                GlassIconButton(
                    icon = {
                        AnimatedVisibility(visible = isRecording) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Остановить запись",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        AnimatedVisibility(visible = !isRecording) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Голосовое сообщение",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                                    .graphicsLayer {
                                        scaleX = recordScale
                                        scaleY = recordScale
                                    }
                            )
                        }
                    }
                },
                onClick = {
                    if (!isRecording) {
                        isRecording = true
                        recordScale = 1.2f
                        onVoiceRecordStart?.invoke()
                    } else {
                        isRecording = false
                        recordScale = 1f
                        onVoiceRecordStop?.invoke()
                    }
                },
                size = 44.dp,
                variant = if (isRecording) GlassButtonVariant.Destructive else GlassButtonVariant.Secondary
            )
            } else {
                // Send button
                AnimatedContent(
                    targetState = hasText,
                    label = "send_button",
                    transitionSpec = {
                        scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) togetherWith
                                scaleOut()
                    }
                ) { hasInput ->
                    if (hasInput) {
                        GlassIconButton(
                            icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) },
                            onClick = onSend,
                            size = 44.dp,
                            variant = GlassButtonVariant.Primary
                        )
                    }
                }
            }
        }
    }
}

fun formatMessageTime(timestamp: Long): String {
    val cal = timestamp / 1000
    val hours = (cal / 3600) % 24
    val minutes = (cal / 60) % 60
    return "%02d:%02d".format(hours, minutes)
}