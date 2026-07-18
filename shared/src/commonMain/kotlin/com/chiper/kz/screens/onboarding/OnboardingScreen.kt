package com.chiper.kz.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.glass.GlassButton
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<OnboardingViewModel>()
        val state = viewModel.uiState

        OnboardingContent(
            currentPage = state.currentPage,
            onNext = { viewModel.next() },
            onSkip = { navigator.replace(com.chiper.kz.screens.auth.AuthScreen()) },
            onGetStarted = { navigator.replace(com.chiper.kz.screens.auth.AuthScreen()) }
        )
    }
}

data class OnboardingPage(
    val imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

class OnboardingViewModel : androidx.lifecycle.ViewModel() {
    private var _uiState = mutableStateOf(OnboardingUiState())
    val uiState: OnboardingUiState = _uiState

    private val pages = listOf(
        OnboardingPage(
            imageVector = Icons.Default.ChatBubbleOutline,
            title = "Мгновенные сообщения",
            description = "Отправляйте текст, фото, видео и файлы в реальном времени без задержек",
            primaryColor = TelegramBlue,
            secondaryColor = TelegramLightBlue
        ),
        OnboardingPage(
            imageVector = Icons.Default.Security,
            title = "Полная приватность",
            description = "Сквозное шифрование, таймеры автоуничтожения и анонимные чаты",
            primaryColor = Color(0xFF4DCD5E),
            secondaryColor = Color(0xFF3CC44D)
        ),
        OnboardingPage(
            imageVector = Icons.Default.Group,
            title = "Группы и каналы",
            description = "Создавайте сообщества до 200 000 участников и вещайте для миллионов",
            primaryColor = Color(0xFFFBBC05),
            secondaryColor = Color(0xFFF9A825)
        ),
        OnboardingPage(
            imageVector = Icons.Default.Speed,
            title = "Быстрее всех",
            description = "Распределенная инфраструктура по всему миру — сообщения доходят за миллисекунды",
            primaryColor = Color(0xFF5EB5F7),
            secondaryColor = TelegramBlue
        )
    )

    fun next() {
        val nextPage = (uiState.currentPage + 1) % pages.size
        _uiState.value = _uiState.value.copy(currentPage = nextPage)
    }

    fun skip() {
        // handled in navigator
    }
}

data class OnboardingUiState(
    val currentPage: Int = 0
)

@Composable
fun OnboardingContent(
    currentPage: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onGetStarted: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            imageVector = Icons.Default.ChatBubbleOutline,
            title = "Мгновенные сообщения",
            description = "Отправляйте текст, фото, видео и файлы в реальном времени без задержек",
            primaryColor = TelegramBlue,
            secondaryColor = TelegramLightBlue
        ),
        OnboardingPage(
            imageVector = Icons.Default.Security,
            title = "Полная приватность",
            description = "Сквозное шифрование, таймеры автоуничтожения и анонимные чаты",
            primaryColor = Color(0xFF4DCD5E),
            secondaryColor = Color(0xFF3CC44D)
        ),
        OnboardingPage(
            imageVector = Icons.Default.Group,
            title = "Группы и каналы",
            description = "Создавайте сообщества до 200 000 участников и вещайте для миллионов",
            primaryColor = Color(0xFFFBBC05),
            secondaryColor = Color(0xFFF9A825)
        ),
        OnboardingPage(
            imageVector = Icons.Default.Speed,
            title = "Быстрее всех",
            description = "Распределенная инфраструктура по всему миру — сообщения доходят за миллисекунды",
            primaryColor = Color(0xFF5EB5F7),
            secondaryColor = TelegramBlue
        )
    )

    val page = pages[currentPage]

    var animProgress by remember { mutableFloatStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "icon_scale"
    )
    val rotation by animateFloatAsState(
        targetValue = 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow),
        label = "icon_rotation"
    )

    LaunchedEffect(currentPage) {
        animProgress = 0f
        val anim = animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animProgress = value
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        page.primaryColor.copy(alpha = 0.02f),
                        page.secondaryColor.copy(alpha = 0.01f),
                        Color(0xFF0D1117)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Пропустить",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Animated illustration
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation * 5
                    }
            ) {
                // Background rings
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                center = Offset(0.5f, 0.5f),
                                radius = 0.7f,
                                colors = listOf(
                                    page.primaryColor.copy(alpha = 0.15f * animProgress),
                                    page.secondaryColor.copy(alpha = 0.05f * animProgress),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Main icon
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            Brush.radialGradient(
                                center = Offset(0.3f, 0.3f),
                                radius = 0.8f,
                                colors = listOf(
                                    page.primaryColor.copy(alpha = 0.3f),
                                    page.primaryColor
                                )
                            ),
                            shape = CircleShape
                        )
                        .graphicsLayer {
                            translationX = (1f - animProgress) * 50
                            alpha = animProgress
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.imageVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                // Floating particles
                (0..5).forEach { i ->
                    val offset = (i * 60f + currentPage * 15f + animProgress * 360f) % 360
                    val radius = 160f + (i % 3) * 20f
                    val x = radius * kotlin.math.cos(kotlin.math.toRadians(offset.toDouble()))
                    val y = radius * kotlin.math.sin(kotlin.math.toRadians(offset.toDouble()))

                    Box(
                        modifier = Modifier
                            .offset(x = (x / 280 * 100).dp, y = (y / 280 * 100).dp)
                            .size(8.dp + (i % 3).dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        page.primaryColor.copy(alpha = 0.6f),
                                        page.primaryColor.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .graphicsLayer { alpha = animProgress * (0.5f + (i % 3) * 0.15f) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { if (it > currentPage) -40 else 40 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) togetherWith
                    fadeOut(tween(200)) + slideOutVertically(
                        targetOffsetY = { if (it > currentPage) 40 else -40 },
                        animationSpec = tween(200)
                    )
                }
            ) { pageIndex ->
                Text(
                    text = pages[pageIndex].title,
                    style = GlassTypography.DisplaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn(tween(300, delayMillis = 100)) + slideInVertically(
                        initialOffsetY = { if (it > currentPage) -30 else 30 },
                        animationSpec = tween(300, delayMillis = 100, easing = FastOutSlowInEasing)
                    ) togetherWith
                    fadeOut(tween(200)) + slideOutVertically(
                        targetOffsetY = { if (it > currentPage) 30 else -30 },
                        animationSpec = tween(200)
                    )
                }
            ) { pageIndex ->
                Text(
                    text = pages[pageIndex].description,
                    style = GlassTypography.BodyLarge.copy(
                        color = TextSecondary,
                        lineHeight = 24.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Page indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                horizontalSpacing = 8.dp
            ) {
                pages.forEachIndexed { index, _ ->
                    val isActive = index == currentPage
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isActive) 28.dp else 8.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                        label = "indicator_$index"
                    )
                    val indicatorColor by animateColorAsState(
                        targetValue = if (isActive) page.primaryColor else TextSecondary.copy(alpha = 0.3f),
                        animationSpec = tween(250),
                        label = "indicator_color_$index"
                    )

                    Box(
                        modifier = Modifier
                            .width(indicatorWidth)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(indicatorColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Next / Get Started button
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn(tween(250)) + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) togetherWith
                    fadeOut(tween(150)) + scaleOut(spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh))
                }
            ) { pageIndex ->
                val isLast = pageIndex == pages.lastIndex
                GlassButton(
                    text = if (isLast) "Начать" else "Далее",
                    onClick = if (isLast) onGetStarted else onNext,
                    icon = { Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) },
                    fullWidth = false
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}