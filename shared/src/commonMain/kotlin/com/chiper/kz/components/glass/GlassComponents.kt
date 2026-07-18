package com.chiper.kz.components.glass

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
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
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = GlassShapes.Card,
    elevation: GlassElevation = GlassElevation.Level1,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press_scale"
    )
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.7f else 1f,
        animationSpec = tween(100),
        label = "press_alpha"
    )

    GlassSurface(
        modifier = modifier
            .scale(pressScale)
            .alpha(pressAlpha)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick?.invoke()
                pressed = false
            }
            .padding(16.dp),
        shape = shape,
        elevation = elevation
    ) {
        content()
    }
}

@Composable
fun GlassButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    variant: GlassButtonVariant = GlassButtonVariant.Primary,
    icon: @Composable (() -> Unit)? = null,
    loading: Boolean = false,
    fullWidth: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = when (variant) {
        GlassButtonVariant.Primary -> GlassButtonColors(
            background = Brush.horizontalGradient(listOf(TelegramBlue, TelegramDarkBlue)),
            contentColor = Color.White,
            borderColor = Color.White.copy(alpha = 0.3f),
            loadingColor = Color.White
        )
        GlassButtonVariant.Secondary -> GlassButtonColors(
            background = Brush.verticalGradient(listOf(
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.06f)
            )),
            contentColor = TextPrimary,
            borderColor = Color.White.copy(alpha = 0.2f),
            loadingColor = TelegramBlue
        )
        GlassButtonVariant.Outline -> GlassButtonColors(
            background = Brush.verticalGradient(listOf(
                Color.Transparent,
                Color.Transparent
            )),
            contentColor = TelegramBlue,
            borderColor = TelegramBlue.copy(alpha = 0.6f),
            loadingColor = TelegramBlue
        )
        GlassButtonVariant.Ghost -> GlassButtonColors(
            background = Brush.verticalGradient(listOf(
                Color.White.copy(alpha = 0.04f),
                Color.Transparent
            )),
            contentColor = TextPrimary,
            borderColor = Color.Transparent,
            loadingColor = TelegramBlue
        )
        GlassButtonVariant.Destructive -> GlassButtonColors(
            background = Brush.horizontalGradient(listOf(ErrorRed, Color(0xFFC62828))),
            contentColor = Color.White,
            borderColor = Color.White.copy(alpha = 0.3f),
            loadingColor = Color.White
        )
    }

    val elevation = when (variant) {
        GlassButtonVariant.Outline, GlassButtonVariant.Ghost -> GlassElevation.Level0
        else -> GlassElevation.Level2
    }

    val pressedScale by animateFloatAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label = "btn_press"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 2.dp else 1.dp,
        animationSpec = tween(150),
        label = "border_width"
    )

    val modifierWithWidth = if (fullWidth) modifier.fillMaxWidth() else modifier

    GlassSurface(
        modifier = modifierWithWidth
            .scale(pressedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading
            ) { onClick() }
            .padding(vertical = 14.dp, horizontal = 24.dp)
            .alpha(if (enabled) 1f else 0.5f),
        shape = GlassShapes.Button,
        elevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colors.loadingColor,
                    strokeWidth = 2.5.dp
                )
            } else {
                icon?.invoke()
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.contentColor
                )
            }
        }
    }
}

@Composable
fun GlassIconButton(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: Dp = 44.dp,
    elevation: GlassElevation = GlassElevation.Level1,
    variant: GlassButtonVariant = GlassButtonVariant.Secondary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = when (variant) {
        GlassButtonVariant.Primary -> GlassButtonColors(
            background = Brush.horizontalGradient(listOf(TelegramBlue, TelegramDarkBlue)),
            contentColor = Color.White,
            borderColor = Color.White.copy(alpha = 0.3f),
            loadingColor = Color.White
        )
        else -> GlassButtonColors(
            background = Brush.verticalGradient(listOf(
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.04f)
            )),
            contentColor = TextPrimary,
            borderColor = Color.White.copy(alpha = 0.15f),
            loadingColor = TelegramBlue
        )
    }

    val pressedScale by animateFloatAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label = "icon_btn_press"
    )

    GlassSurface(
        modifier = modifier
            .size(size)
            .scale(pressedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) { onClick() }
            .alpha(if (enabled) 1f else 0.4f),
        shape = GlassShapes.Button,
        elevation = elevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

enum class GlassButtonVariant {
    Primary, Secondary, Outline, Ghost, Destructive
}

data class GlassButtonColors(
    val background: Brush,
    val contentColor: Color,
    val borderColor: Color,
    val loadingColor: Color
)

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    focusRequester: FocusRequester? = null,
    enabled: Boolean = true
) {
    var focused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val containerColor = if (isError) GlassColors.ErrorTint
    else if (focused) GlassColors.PrimaryTint
    else GlassColors.SurfaceTint

    val borderColor = when {
        isError -> ErrorRed.copy(alpha = 0.5f)
        focused -> TelegramBlue.copy(alpha = 0.6f)
        else -> Color.White.copy(alpha = 0.12f)
    }

    val borderWidth = if (focused || isError) 1.5.dp else 1.dp

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester ?: FocusRequester())
            .onFocusChanged { focused = it.isFocused }
            .padding(vertical = 4.dp),
        shape = GlassShapes.Field,
        elevation = if (focused) GlassElevation.Level2 else GlassElevation.Level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                if (value.isNotBlank() || focused) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (focused) TelegramBlue else TextSecondary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                TextField(
                    value = value,
                    onValueChange = { onValueChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Normal
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedBackgroundColor = Color.Transparent,
                        unfocusedBackgroundColor = Color.Transparent,
                        cursorColor = TelegramBlue,
                        textColor = TextPrimary,
                        placeholderColor = TextSecondary.copy(alpha = 0.5f),
                        leadingIconColor = if (focused) TelegramBlue else TextSecondary,
                        trailingIconColor = if (focused) TelegramBlue else TextSecondary
                    ),
                    placeholder = { if (!focused && value.isBlank()) Text(label, color = TextSecondary.copy(alpha = 0.5f), fontSize = 16.sp) }
                )
            }

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
            }
        }
    }

    AnimatedVisibility(visible = isError && errorText != null) {
        Text(
            text = errorText!!,
            fontSize = 11.sp,
            color = ErrorRed,
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun GlassChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    variant: GlassChipVariant = GlassChipVariant.Default
) {
    val colors = when (variant) {
        GlassChipVariant.Default -> if (selected)
            GlassButtonColors(
                background = Brush.horizontalGradient(listOf(TelegramBlue.copy(alpha = 0.3f), TelegramDarkBlue.copy(alpha = 0.2f))),
                contentColor = TelegramBlue,
                borderColor = TelegramBlue.copy(alpha = 0.4f),
                loadingColor = TelegramBlue
            )
            else
            GlassButtonColors(
                background = Brush.verticalGradient(listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.04f)
                )),
                contentColor = TextSecondary,
                borderColor = Color.White.copy(alpha = 0.1f),
                loadingColor = TelegramBlue
            )
        GlassChipVariant.Primary -> if (selected)
            GlassButtonColors(
                background = Brush.horizontalGradient(listOf(TelegramBlue, TelegramDarkBlue)),
                contentColor = Color.White,
                borderColor = Color.White.copy(alpha = 0.3f),
                loadingColor = Color.White
            )
            else
            GlassButtonColors(
                background = Brush.verticalGradient(listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.04f)
                )),
                contentColor = TextPrimary,
                borderColor = Color.White.copy(alpha = 0.1f),
                loadingColor = TelegramBlue
            )
    }

    val elevation = if (selected) GlassElevation.Level2 else GlassElevation.Level0

    GlassSurface(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        shape = GlassShapes.Chip,
        elevation = elevation
    ) {
        Row(
            modifier = Modifier.wrapContentSize(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.invoke()
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = colors.contentColor
            )
        }
    }
}

enum class GlassChipVariant { Default, Primary }

@Composable
fun GlassFAB(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    variant: GlassFABVariant = GlassFABVariant.Primary,
    size: Dp = 56.dp,
    extendedText: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    val colors = when (variant) {
        GlassFABVariant.Primary -> GlassButtonColors(
            background = Brush.radialGradient(
                center = Offset(0.5f, 0.3f),
                radius = 0.8f,
                colors = listOf(
                    TelegramBlue.copy(alpha = 0.9f),
                    TelegramDarkBlue
                )
            ),
            contentColor = Color.White,
            borderColor = Color.White.copy(alpha = 0.25f),
            loadingColor = Color.White
        )
        GlassFABVariant.Secondary -> GlassButtonColors(
            background = Brush.verticalGradient(listOf(
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = 0.06f)
            )),
            contentColor = TextPrimary,
            borderColor = Color.White.copy(alpha = 0.2f),
            loadingColor = TelegramBlue
        )
    }

    val pressedScale by animateFloatAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label = "fab_press"
    )

    val rotation by animateFloatAsState(
        targetValue = 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "fab_rotation"
    )

    GlassSurface(
        modifier = modifier
            .size(size)
            .scale(pressedScale)
            .rotate(rotation)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(16.dp),
        shape = if (extendedText != null) GlassShapes.Card else GlassShapes.Button,
        elevation = GlassElevation.Floating
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .padding(horizontal = if (extendedText != null) 20.dp else 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            extendedText?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.contentColor
                )
            }
        }
    }
}

enum class GlassFABVariant { Primary, Secondary }

@Composable
fun GlassTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcons: List<@Composable (() -> Unit)> = emptyList(),
    onLeadingClick: (() -> Unit)? = null,
    subtitle: String? = null,
    elevation: GlassElevation = GlassElevation.Level1
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = GlassShapes.Card,
        elevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                IconButton(onClick = onLeadingClick ?: {}) {
                    Box(
                        modifier = Modifier.size(40.dp).wrapContentSize(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        it()
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                trailingIcons.forEach { icon ->
                    IconButton(onClick = {}) {
                        Box(
                            modifier = Modifier.size(40.dp).wrapContentSize(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            icon()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassBottomNavBar(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    items: List<GlassBottomNavItem>,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level3
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "nav_scale_$index"
                )
                val alpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.6f,
                    animationSpec = tween(200),
                    label = "nav_alpha_$index"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemClick(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .wrapContentSize(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) TelegramBlue else TextSecondary
                            )
                        }
                        AnimatedVisibility(visible = isSelected) {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TelegramBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

data class GlassBottomNavItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)

@Composable
fun GlassModalBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(tween(150)) + slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(200)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onDismiss() }
            )
            GlassSurface(
                modifier = Modifier
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
fun GlassSheetContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}