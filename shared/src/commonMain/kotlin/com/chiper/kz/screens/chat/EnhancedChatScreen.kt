package com.chiper.kz.screens.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.pointerInput
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.chat.*
import com.chiper.kz.components.glass.*
import com.chiper.kz.model.Message
import com.chiper.kz.model.MessageType
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType
import com.chiper.kz.utils.rememberHapticFeedback
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
        val haptic = rememberHapticFeedback()

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
                    ChatTopAppBar(
                        chatName = chatName,
                        avatarUrl = avatarUrl,
                        isTyping = viewModel.isTyping,
                        onBackClick = { navigator.pop() },
                        onCallClick = { },
                        onVideoClick = { },
                        onMenuClick = { viewModel.showChatMenu() }
                    )
                },
                containerColor = ChatBackground
            ) { padding ->
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
                                        EnhancedMessageBubble(
                                            message = message,
                                            onReply = { viewModel.replyToMessage(message) },
                                            onForward = { viewModel.forwardMessage(message) },
                                            onCopy = { viewModel.copyMessage(message) },
                                            onPin = { viewModel.pinMessage(message) },
                                            onDelete = { viewModel.deleteMessage(message) },
                                            onReactionClick = { emoji -> viewModel.addReaction(message, emoji) },
                                            onReactionLongPress = { emoji -> viewModel.showReactionPicker(message) },
                                            haptic = rememberHapticFeedback()
                                        )
                                    }
                                }
                            }

                            if (viewModel.isTyping) {
                                item {
                                    TypingIndicatorItem()
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
                        onVoiceStart = { viewModel.startVoiceRecording() },
                        onVoiceStop = { viewModel.stopVoiceRecording() },
                        onAttachClick = { viewModel.showAttachMenu() },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatTopAppBar(
    chatName: String,
    avatarUrl: String,
    isTyping: Boolean,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onVideoClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    GlassTopAppBar(
        title = chatName,
        subtitle = if (isTyping) "печатает..." else "в сети",
        leadingIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
            }
        },
        trailingIcons = listOf(
            { IconButton(onClick = onCallClick) { Icon(Icons.Default.Call, contentDescription = "Звонок", tint = Color.White) } },
            { IconButton(onClick = onVideoClick) { Icon(Icons.Default.Videocam, contentDescription = "Видеозвонок", tint = Color.White) } },
            { IconButton(onClick = onMenuClick) { Icon(Icons.Default.MoreVert, contentDescription = "Меню", tint = Color.White) } }
        )
    )
}

@Composable
fun TypingIndicatorItem() {
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

@Composable
fun EnhancedMessageBubble(
    message: Message,
    onReply: () -> Unit,
    onForward: () -> Unit,
    onCopy: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onReactionClick: (String) -> Unit,
    onReactionLongPress: (String) -> Unit,
    haptic: HapticFeedback
) {
    val isSent = message.isSentByMe
    var offsetX by remember { mutableStateOf(0f) }
    var isSwipeOpen by remember { mutableStateOf(false) }
    var showReactions by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val maxSwipeDistance = 120f
    val swipeThreshold = 60f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isSent) 64.dp else 8.dp,
                end = if (isSent) 8.dp else 64.dp,
                top = 1.dp,
                bottom = 1.dp
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
            .offset(x = offsetX.dp)
    ) {
        // Swipe actions
        if (isSent && isSwipeOpen) {
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
                        onReply()
                    }
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Reply, contentDescription = "Reply", tint = Color.White)
                    Text(text = "Ответить", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Message bubble
        GlassSurface(
            modifier = Modifier
                .widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (isSent) 16.dp else 4.dp,
                topEnd = if (isSent) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            elevation = GlassElevation.Level1
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                // Reply preview
                message.replyTo?.let { replyId ->
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), GlassShapes.Small)
                            .padding(8.dp),
                        shape = GlassShapes.Small,
                        elevation = GlassElevation.Level0
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.5.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ответ на сообщение",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Message content with markdown
                MarkdownMessage(
                    text = message.text,
                    color = if (isSent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15,
                    lineHeight = 20
                )

                // Reactions
                if (message.reactions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    MessageReactions(
                        reactions = message.reactions,
                        onReactionClick = onReactionClick,
                        onReactionAdd = { emoji -> /* add reaction */ },
                        message = message,
                        isSent = isSent
                    )
                }

                // Time and status
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatMessageTime(message.timestamp),
                        fontSize = 11.sp,
                        color = if (isSent) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    if (isSent) {
                        Icon(
                            imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = if (message.isRead) "Прочитано" else "Отправлено",
                            modifier = Modifier.size(14.dp),
                            tint = if (message.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
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
                    onReactionClick(emoji)
                },
                onDismiss = { showReactions = false },
                isSent = isSent
            )
        }
    }
}

@Composable
fun ReactionPicker(
    message: Message,
    onReactionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isSent: Boolean
) {
    val reactions = listOf("❤️", "👍", "👎", "😂", "😮", "😢", "🔥", "🎉", "💩", "🤔")
    val addedReactions = message.reactions.keys.toSet()

    Box(
        modifier = Modifier
            .padding(horizontal = if (isSent) 64.dp else 8.dp, vertical = 4.dp)
    ) {
        GlassSurface(
            modifier = Modifier
                .padding(if (isSent) PaddingValues(end = 64.dp) else PaddingValues(start = 64.dp))
                .wrapContentSize(),
            shape = GlassShapes.Card,
            elevation = GlassElevation.Level3
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                reactions.forEachIndexed { index, emoji ->
                    val isAdded = emoji in addedReactions
                    val scale by animateFloatAsState(
                        targetValue = if (isAdded) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "picker_${emoji}_scale"
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .size(40.dp)
                            .background(
                                if (isAdded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                GlassShapes.Circle
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onReactionSelected(emoji)
                            }
                            .wrapContentSize(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageReactions(
    reactions: Map<String, Int>,
    onReactionClick: (String) -> Unit,
    onReactionAdd: (String) -> Unit,
    message: Message,
    isSent: Boolean
) {
    val sortedReactions = reactions.toList().sortedByDescending { it.second }

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        sortedReactions.forEach { (emoji, count) ->
            val isReactedByMe = true // Check if current user reacted
            val scale by animateFloatAsState(
                targetValue = if (isReactedByMe) 1.15f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "reaction_${emoji}_scale"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .background(
                        if (isSent) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        GlassShapes.Chip
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onReactionClick(emoji) }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = emoji, fontSize = 13.sp)
                    if (count > 1) {
                        Text(
                            text = count.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

fun formatMessageTime(timestamp: Long): String {
    val cal = timestamp / 1000
    val hours = (cal / 3600) % 24
    val minutes = (cal / 60) % 60
    return "%02d:%02d".format(hours, minutes)
}