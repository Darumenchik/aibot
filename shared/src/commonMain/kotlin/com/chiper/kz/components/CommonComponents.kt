package com.chiper.kz.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.theme.TextSecondary
import com.chiper.kz.theme.TelegramBlue
import kotlinx.coroutines.delay

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val dots = listOf(0, 1, 2)
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        dots.forEach { index ->
            var startAnimation by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(index * 200L)
                startAnimation = true
            }
            val offsetY by animateFloatAsState(
                targetValue = if (startAnimation) -6f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_bounce"
            )
            Box(
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(TelegramBlue.copy(alpha = 0.6f))
            )
        }
    }
}

@Composable
fun OnlineBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF4DCD5E))
    )
}

@Composable
fun UnreadBadge(count: Int, modifier: Modifier = Modifier) {
    if (count > 0) {
        Box(
            modifier = modifier
                .sizeIn(minWidth = 22.dp, minHeight = 22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(TelegramBlue)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 60000
    val hours = diff / 3600000
    val days = diff / 86400000

    return when {
        minutes < 1 -> "сейчас"
        minutes < 60 -> "${minutes} мин"
        hours < 24 -> "${hours} ч"
        days < 7 -> "${days} дн"
        else -> {
            val secs = timestamp / 1000
            val day = (secs / 86400) % 7
            val names = arrayOf("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
            names.getOrElse(day.toInt()) { "" }
        }
    }
}

fun formatMessageTime(timestamp: Long): String {
    val cal = timestamp / 1000
    val hours = (cal / 3600) % 24
    val minutes = (cal / 60) % 60
    return "%02d:%02d".format(hours, minutes)
}

fun getInitials(name: String): String {
    val parts = name.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}"
        parts.isNotEmpty() -> parts[0].take(2)
        else -> "?"
    }
}

fun avatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFFE17076),
        Color(0xFF7BC862),
        Color(0xFFE5CA77),
        Color(0xFF65AADD),
        Color(0xFFA695E7),
        Color(0xFFEE7AAE),
        Color(0xFF6EC9CB),
        Color(0xFFFAA774),
    )
    val index = name.hashCode().let { if (it < 0) -it else it } % colors.size
    return colors[index]
}
