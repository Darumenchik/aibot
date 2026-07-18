package com.chiper.kz.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawRect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.theme.glass.*
import com.chiper.kz.theme.*
import kotlinx.coroutines.delay

@Composable
fun GlassSkeleton(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = GlassShapes.Card,
    elevation: GlassElevation = GlassElevation.Level1,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified
) {
    val shimmerAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val baseColor = elevation.surfaceAlpha
    val highlightColor = baseColor + 0.1f

    val animatedColor by animateColorAsState(
        targetValue = Color.White.copy(alpha = highlightColor),
        animationSpec = tween(1500),
        label = "skeleton_color"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = baseColor),
                        animatedColor,
                        Color.White.copy(alpha = baseColor)
                    ),
                    start = Offset(-size.width * shimmerAlpha, 0f),
                    end = Offset(size.width * (1 + shimmerAlpha), 0f)
                ),
                shape = shape
            )
            .clip(shape)
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = elevation.borderAlpha * 0.5f),
                            Color.White.copy(alpha = elevation.borderAlpha)
                        ),
                        start = Offset.Zero,
                        end = Offset(0f, size.height)
                    ),
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = elevation.borderWidth.toPx())
                )
            },
        contentAlignment = Alignment.Center
    )
}

@Composable
fun GlassSkeleton(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = GlassShapes.Card,
    elevation: GlassElevation = GlassElevation.Level1,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified
) {
    val shimmerAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val baseColor = elevation.surfaceAlpha
    val highlightColor = baseColor + 0.1f

    val animatedColor by animateColorAsState(
        targetValue = Color.White.copy(alpha = highlightColor),
        animationSpec = tween(1500),
        label = "skeleton_color"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = baseColor),
                        animatedColor,
                        Color.White.copy(alpha = baseColor)
                    ),
                    start = Offset(-size.width * shimmerAlpha, 0f),
                    end = Offset(size.width * (1 + shimmerAlpha), 0f)
                ),
                shape = shape
            )
            .clip(shape)
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = elevation.borderAlpha * 0.5f),
                            Color.White.copy(alpha = elevation.borderAlpha)
                        ),
                        start = Offset.Zero,
                        end = Offset(0f, size.height)
                    ),
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = elevation.borderWidth.toPx())
                )
            },
        contentAlignment = Alignment.Center
    )
}

@Composable
fun GlassSkeletonProfile(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassSkeleton(
            modifier = Modifier
                .size(100.dp)
                .clip(androidx.compose.foundation.shape.CircleShape),
            shape = androidx.compose.foundation.shape.CircleShape,
            elevation = GlassElevation.Floating
        )
        Spacer(modifier = Modifier.height(16.dp))
        GlassSkeleton(
            modifier = Modifier
                .width(120.dp)
                .height(24.dp),
            shape = GlassShapes.Chip,
            elevation = GlassElevation.Level1
        )
        Spacer(modifier = Modifier.height(8.dp))
        GlassSkeleton(
            modifier = Modifier
                .width(200.dp)
                .height(16.dp),
            shape = GlassShapes.Chip,
            elevation = GlassElevation.Level0
        )
        Spacer(modifier = Modifier.height(24.dp))
        repeat(3) { i ->
            GlassSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = GlassShapes.Card,
                elevation = GlassElevation.Level1,
                height = 56.dp
            )
            if (i < 2) Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GlassSkeletonGroupList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(modifier = modifier) {
        repeat(itemCount) { i ->
            GlassSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = GlassShapes.Card,
                elevation = GlassElevation.Level1,
                height = 72.dp
            )
            if (i < itemCount - 1) Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun GlassSkeletonChannelList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(modifier = modifier) {
        repeat(itemCount) { i ->
            GlassSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = GlassShapes.Card,
                elevation = GlassElevation.Level1,
                height = 72.dp
            )
            if (i < itemCount - 1) Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun GlassSkeletonSettingsSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    Column(modifier = modifier) {
        GlassSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, top = 8.dp, bottom = 4.dp),
            shape = GlassShapes.Chip,
            elevation = GlassElevation.Level0,
            height = 16.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        repeat(itemCount) { i ->
            GlassSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = GlassShapes.Card,
                elevation = GlassElevation.Level1,
                height = 56.dp
            )
        }
    }
}

@Composable
fun GlassSkeletonChatList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(modifier = modifier) {
        repeat(itemCount) { i ->
            GlassSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = GlassShapes.Card,
                elevation = GlassElevation.Level1,
                height = 80.dp
            )
            if (i < itemCount - 1) Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun GlassSkeletonMessageList(
    modifier: Modifier = Modifier,
    itemCount: Int = 10
) {
    Column(modifier = modifier) {
        repeat(itemCount) { i ->
            val isSent = i % 2 == 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
            ) {
                GlassSkeleton(
                    modifier = Modifier
                        .widthIn(min = 80.dp, max = 280.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(
                        topStart = if (isSent) 16.dp else 4.dp,
                        topEnd = if (isSent) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ),
                    elevation = GlassElevation.Level1,
                    height = (16 + (i % 3) * 8).dp
                )
            }
        }
    }
}