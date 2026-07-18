package com.chiper.kz.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.MainTabScreen
import com.chiper.kz.components.glass.*
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class AuthScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<AuthViewModel>()

        val state = viewModel.uiState
        val focusManager = LocalFocusManager.current
        val passwordFocusRequester = remember { FocusRequester() }
        var passwordVisible by remember { mutableStateOf(false) }

        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) {
                navigator.replaceAll(MainTabScreen())
            }
        }

        AuthScreenContent(
            state = state,
            onEmailChanged = { viewModel.onEmailChanged(it) },
            onPasswordChanged = { viewModel.onPasswordChanged(it) },
            onNameChanged = { viewModel.onNameChanged(it) },
            onTabChanged = { viewModel.onTabChanged(it) },
            onSubmit = {
                focusManager.clearFocus()
                viewModel.onSubmit()
            },
            onGoogleLogin = { viewModel.onGoogleLogin() }
        )
    }
}

@Composable
fun AuthScreenContent(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onTabChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onGoogleLogin: () -> Unit
) {
    val passwordFocusRequester = remember { FocusRequester() }
    var passwordVisible by remember { mutableStateOf(false) }

    GlassBackground(animated = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Logo with glass effect
                GlassSurface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    elevation = GlassElevation.Floating
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    center = Offset(0.3f, 0.3f),
                                    radius = 0.8f,
                                    colors = listOf(
                                        TelegramBlue.copy(alpha = 0.3f),
                                        TelegramBlue,
                                        TelegramDarkBlue
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "C",
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Chiper",
                    style = GlassTypography.DisplayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Быстрый. Безопасный. Твой.",
                    style = GlassTypography.BodyMedium.copy(
                        color = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Tab Selector
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
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
                            text = "Вход",
                            selected = state.isLoginTab,
                            onClick = { onTabChanged(true) }
                        )
                        GlassTabItem(
                            text = "Регистрация",
                            selected = !state.isLoginTab,
                            onClick = { onTabChanged(false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form fields
                AnimatedVisibility(
                    visible = !state.isLoginTab,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)) + slideInVertically(initialOffsetY = { -20 }),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200)) + slideOutVertically(targetOffsetY = { -20 })
                ) {
                    Column {
                        GlassTextField(
                            value = state.name,
                            onValueChange = onNameChanged,
                            label = "Имя",
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = TelegramBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() })
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                GlassTextField(
                    value = state.email,
                    onValueChange = onEmailChanged,
                    label = "Email",
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = TelegramBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.EmailAddress,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() })
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlassTextField(
                    value = state.password,
                    onValueChange = onPasswordChanged,
                    label = "Пароль",
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = TelegramBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль",
                                tint = TextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() })
                )

                // Error text
                AnimatedVisibility(visible = state.error != null) {
                    Text(
                        text = state.error ?: "",
                        color = Color(0xFFE53935),
                        style = GlassTypography.BodySmall,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                GlassButton(
                    text = if (state.isLoginTab) "Войти" else "Создать аккаунт",
                    onClick = onSubmit,
                    fullWidth = true,
                    loading = state.isLoading,
                    leadingIcon = { Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                    Text(
                        text = "или",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = TextSecondary,
                        style = GlassTypography.BodyMedium
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google login button
                GlassButton(
                    text = "Войти через Google",
                    onClick = onGoogleLogin,
                    variant = GlassButtonVariant.Outline,
                    fullWidth = true,
                    loading = state.isLoading,
                    leadingIcon = {
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))
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
            .padding(vertical = 14.dp),
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