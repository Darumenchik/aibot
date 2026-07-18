package com.chiper.kz.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.pointerInput
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ComposedModifierTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.model.Message
import com.chiper.kz.theme.ChiperColorScheme
import com.chiper.kz.theme.glass.GlassElevation
import com.chiper.kz.theme.glass.GlassSurface
import com.chiper.kz.theme.glass.GlassShapes
import com.chiper.kz.utils.HapticFeedback
import com.chiper.kz.utils.HapticType
import com.chiper.kz.utils.rememberHapticFeedback
import kotlinx.coroutines.launch

@Composable
fun GlassMessageBubble(
    message: Message,
    onReply: ((Message) -> Unit)? = null,
    onLongPress: ((Message) -> Unit)? = nil,
    onReactionClick: ((Message, String) -> Unit)? = nil,
    onReactionAdd: ((Message, String) -> Unit)? = nil,
    isSelected: Boolean = false,
    showTime: Boolean = true,
    showStatus: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isSent = message.isSentByMe
    val haptic = rememberHapticFeedback()
    var offsetX by remember { mutableStateOf(0f) }
    var isSwipeOpen by remember { mutableStateOf(false) }
    var showReactions by remember { mutableStateOf(false) }
    val reactionAnimations = remember {
        mutableStateMapOf<String, Float>()
    }

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
                detectTapGestures(
                    onLongPress = {
                        if (!isSwipeOpen) {
                            haptic.trigger(HapticType.Medium)
                            onLongPress?.invoke(message)
                        }
                    },
                    onDoubleTap = {
                        // Quick reaction
                        haptic.trigger(HapticType.Selection)
                        onReactionAdd?.invoke(message, "❤️")
                    }
                )
            }
            .offset(x = offsetX.dp)
            .pointerInput(Unit) {
                if (!isSent || isSwipeOpen) return@pointerInput
                drag(
                    onDragStart = { },
                    onDrag = { change ->
                        change.consume()
                        val newOffset = (offsetX + change.positionChange().x).coerceIn(-maxSwipeDistance, 0f)
                        offsetX = newOffset
                        isSwipeOpen = newOffset < -swipeThreshold
                    },
                    onDragEnd = {
                        if (isSwipeOpen) {
                            // Keep open
                        } else {
                            animateOffsetTo(0f)
                        }
                    },
                    onDragCancel = {
                        animateOffsetTo(0f)
                    }
                )
            }
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
                        animateOffsetTo(0f)
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
                // Message content with markdown
                MarkdownText(
                    text = message.text,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
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
                            .padding(top = 2.dp),
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

        // Reactions panel (appears on long press)
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

    fun animateOffsetTo(target: Float) {
        val anim = animateFloatAsState(
            targetValue = target,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            label = "swipe_offset"
        )
        // Note: In real implementation, you'd use a coroutine to animate
        offsetX = target
        isSwipeOpen = target < -swipeThreshold
    }
}

@Composable
fun MessageReactions(
    reactions: Map<String, Int>,
    onReactionClick: ((Message, String) -> Unit)?,
    onReactionAdd: ((Message, String) -> Unit)?,
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
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "reaction_${emoji}_scale"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .background(
                        if (isSent) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        GlassShapes.Chip
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onReactionClick?.invoke(message, emoji) }
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
fun MarkdownText(
    text: String,
    color: Color,
    fontSize: Int = 15,
    lineHeight: Int = 20
) {
    // Simple markdown-like rendering
    val parts = parseMarkdown(text)

    Text(
        text = buildAnnotatedString {
            parts.forEach { (content, style) ->
                withStyle(style) {
                    append(content)
                }
            }
        },
        color = color,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        overflow = TextOverflow.Ellipsis
    )
}

fun parseMarkdown(text: String): List<Pair<String, SpanStyle>> {
    val result = mutableListOf<Pair<String, SpanStyle>>()
    var remaining = text
    val boldRegex = "\\*\\*(.+?)\\*\\*".toRegex()
    val italicRegex = "\\*(.+?)\\*".toRegex()
    val codeRegex = "`(.+?)`".toRegex()
    val linkRegex = "\\[(.+?)\\]\\((.+?)\\)".toRegex()

    // Simplified - in production use a proper markdown parser
    if (boldRegex.containsMatchIn(remaining)) {
        boldRegex.findAll(remaining).forEach { match ->
            val before = remaining.substring(0, match.range.start)
            if (before.isNotBlank()) result += before to SpanStyle()
            result += match.groupValues[1] to SpanStyle(fontWeight = FontWeight.Bold)
            remaining = remaining.substring(match.range.endInclusive + 1)
        }
    }

    if (result.isEmpty()) {
        result += text to SpanStyle()
    }

    return result
}

fun formatMessageTime(timestamp: Long): String {
    val cal = timestamp / 1000
    val hours = (cal / 3600) % 24
    val minutes = (cal / 60) % 60
    return "%02d:%02d".format(hours, minutes)
}