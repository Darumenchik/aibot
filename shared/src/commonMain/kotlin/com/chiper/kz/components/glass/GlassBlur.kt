package com.chiper.kz.components.glass

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ComposedModifierTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.DrawScope
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.px
import com.chiper.kz.theme.glass.GlassElevation
import com.chiper.kz.theme.glass.GlassShapes
import com.chiper.kz.theme.glass.GlassSurface
import kotlinx.coroutines.delay

@Composable
fun GlassBlurBackground(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 20.dp,
    overlayColor: Color = Color.Black.copy(alpha = 0.3f),
    animated: Boolean = true
) {
    val context = LocalContext.current
    val alpha by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "blur_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(BlurModifier(blurRadius, overlayColor, alpha))
    )
}

private class BlurModifier(
    private val blurRadius: Dp,
    private val overlayColor: Color,
    private val alpha: Float
) : DrawModifier, ComposedModifierTag {
    override fun ContentDrawScope.draw() {
        drawRect(
            color = overlayColor.copy(alpha = overlayColor.alpha * alpha),
            size = size
        )
        // Note: Actual blur requires RenderEffect API (API 31+)
        // For older APIs, we use a semi-transparent overlay as fallback
        this@BlurModifier.drawContent()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlurModifier) return false
        return blurRadius == other.blurRadius &&
               overlayColor == other.overlayColor &&
               alpha == other.alpha
    }

    override fun hashCode(): Int {
        var result = blurRadius.hashCode()
        result = 31 * result + overlayColor.hashCode()
        result = 31 * result + alpha.hashCode()
        return result
    }
}

@Composable
fun GlassModalBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(tween(150)) + slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(250)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background overlay with blur
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onDismiss() }
            )

            // Modal content
            GlassSurface(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .heightIn(min = 100.dp, max = 500.dp),
                shape = GlassShapes.Modal,
                elevation = GlassElevation.Modal
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun GlassBottomSheetScaffold(
    sheetContent: @Composable () -> Unit,
    sheetPeekHeight: Dp = 100.dp,
    modifier: Modifier = Modifier,
    scaffoldContent: @Composable (PaddingValues) -> Unit
) {
    var sheetState by remember { mutableStateOf(BottomSheetState.Collapsed) }
    var dragOffset by remember { mutableStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        scaffoldContent(WindowInsets.safeDrawing.asPaddingValues())

        // Draggable bottom sheet
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (sheetPeekHeight - dragOffset).toPx())
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset += dragAmount.y
                            dragOffset = dragOffset.coerceIn(0f, sheetPeekHeight.toPx())
                        },
                        onDragEnd = {
                            if (dragOffset > sheetPeekHeight.toPx() / 2) {
                                sheetState = BottomSheetState.Expanded
                                animateDragTo(0f)
                            } else {
                                sheetState = BottomSheetState.Collapsed
                                animateDragTo(sheetPeekHeight.toPx())
                            }
                        },
                        onDragCancel = {
                            animateDragTo(sheetPeekHeight.toPx())
                        }
                    )
                },
                shape = GlassShapes.Modal,
                elevation = GlassElevation.Modal
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    sheetContent()
                }
            }
        }
    }

    fun animateDragTo(targetOffset: Float) {
        // Implementation would animate dragOffset to targetOffset
    }
}

enum class BottomSheetState {
    Collapsed, Expanded, Hidden
}

@Composable
fun GlassOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(150))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(BlurModifier(20.dp, Color.Black.copy(alpha = 0.5f), 1f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            GlassSurface(
                modifier = modifier
                    .padding(horizontal = 24.dp)
                    .wrapContentSize(Alignment.Center),
                shape = GlassShapes.Modal,
                elevation = GlassElevation.Modal
            ) {
                content()
            }
        }
    }
}

@Composable
fun GlassTooltip(
    text: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    position: TooltipPosition = TooltipPosition.Top
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(150)) + scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = fadeOut(tween(100)) + scaleOut(animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy))
    ) {
        GlassSurface(
            modifier = modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .wrapContentSize(Alignment.Center),
            shape = GlassShapes.Small,
            elevation = GlassElevation.Level3
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color.White,
                style = androidx.compose.material3.Typography().bodySmall
            )
        }
    }
}

enum class TooltipPosition {
    Top, Bottom, Start, End
}