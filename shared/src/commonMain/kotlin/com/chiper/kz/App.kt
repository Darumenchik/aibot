package com.chiper.kz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.core.screen.Screen
import com.chiper.kz.screens.auth.AuthScreen
import com.chiper.kz.screens.chatlist.ChatListScreen
import com.chiper.kz.screens.groups.GroupsScreen
import com.chiper.kz.screens.channels.ChannelsScreen
import com.chiper.kz.screens.onboarding.OnboardingScreen
import com.chiper.kz.screens.profile.ProfileScreen
import com.chiper.kz.theme.AppTheme
import com.chiper.kz.theme.ChiperTheme
import com.chiper.kz.theme.AnimatedChiperTheme
import com.chiper.kz.theme.glass.GlassTypography
import com.chiper.kz.utils.KeyboardHandler
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    val themeViewModel = androidx.lifecycle.viewmodel.viewModel<ThemeViewModel>()
    
    AnimatedChiperTheme(viewModel = themeViewModel) {
        var showSplash by remember { mutableStateOf(true) }
        var showOnboarding by remember { mutableStateOf(false) }

        if (showSplash) {
            SplashScreenContent(onSplashFinished = { 
                showSplash = false
                showOnboarding = true
            })
        } else if (showOnboarding) {
            Navigator(screen = OnboardingScreen()) { navigator ->
                // Onboarding handles its own navigation
            }
        } else {
            Navigator(screen = AuthScreen()) { navigator ->
                // Auth handles its own navigation to MainTabScreen
            }
        }
    }
}

@Composable
private fun SplashScreenContent(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "text_alpha"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "subtitle_alpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        com.chiper.kz.theme.SplashGradientStart,
                        com.chiper.kz.theme.SplashGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        color = Color.White,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Chiper",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Быстрый. Безопасный. Твой.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }
    }
}

class MainTabScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(ChatListTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White.copy(alpha = 0.05f),
                        tonalElevation = 0.dp
                    ) {
                        val tabNavigator = LocalTabNavigator.current
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (tabNavigator.current.key == ChatListTab.key)
                                        Icons.Filled.Chat
                                    else Icons.Outlined.Chat,
                                    contentDescription = "Чаты"
                                )
                            },
                            label = { Text("Чаты") },
                            selected = tabNavigator.current.key == ChatListTab.key,
                            onClick = { tabNavigator.current = ChatListTab },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.chiper.kz.theme.TelegramBlue,
                                selectedTextColor = com.chiper.kz.theme.TelegramBlue,
                                unselectedIconColor = com.chiper.kz.theme.TextSecondary,
                                unselectedTextColor = com.chiper.kz.theme.TextSecondary,
                                indicatorColor = com.chiper.kz.theme.TelegramPaleBlue
                            )
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (tabNavigator.current.key == GroupsTab.key)
                                        Icons.Filled.Groups
                                    else Icons.Outlined.Groups,
                                    contentDescription = "Группы"
                                )
                            },
                            label = { Text("Группы") },
                            selected = tabNavigator.current.key == GroupsTab.key,
                            onClick = { tabNavigator.current = GroupsTab },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.chiper.kz.theme.TelegramBlue,
                                selectedTextColor = com.chiper.kz.theme.TelegramBlue,
                                unselectedIconColor = com.chiper.kz.theme.TextSecondary,
                                unselectedTextColor = com.chiper.kz.theme.TextSecondary,
                                indicatorColor = com.chiper.kz.theme.TelegramPaleBlue
                            )
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (tabNavigator.current.key == ChannelsTab.key)
                                        Icons.Filled.Campaign
                                    else Icons.Outlined.Campaign,
                                    contentDescription = "Каналы"
                                )
                            },
                            label = { Text("Каналы") },
                            selected = tabNavigator.current.key == ChannelsTab.key,
                            onClick = { tabNavigator.current = ChannelsTab },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.chiper.kz.theme.TelegramBlue,
                                selectedTextColor = com.chiper.kz.theme.TelegramBlue,
                                unselectedIconColor = com.chiper.kz.theme.TextSecondary,
                                unselectedTextColor = com.chiper.kz.theme.TextSecondary,
                                indicatorColor = com.chiper.kz.theme.TelegramPaleBlue
                            )
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (tabNavigator.current.key == ProfileTab.key)
                                        Icons.Filled.Person
                                    else Icons.Outlined.Person,
                                    contentDescription = "Профиль"
                                )
                            },
                            label = { Text("Профиль") },
                            selected = tabNavigator.current.key == ProfileTab.key,
                            onClick = { tabNavigator.current = ProfileTab },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.chiper.kz.theme.TelegramBlue,
                                selectedTextColor = com.chiper.kz.theme.TelegramBlue,
                                unselectedIconColor = com.chiper.kz.theme.TextSecondary,
                                unselectedTextColor = com.chiper.kz.theme.TextSecondary,
                                indicatorColor = com.chiper.kz.theme.TelegramPaleBlue
                            )
                        )
                    }
                }
            ) {
                CurrentTab()
            }
        }
    }
}

object ChatListTab : Tab {
    private fun readResolve(): Any = ChatListTab

    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 0u, title = "Чаты", icon = Icons.Outlined.Chat)
        }

    @Composable
    override fun Content() {
        ChatListScreen().Content()
    }
}

object GroupsTab : Tab {
    private fun readResolve(): Any = GroupsTab

    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 1u, title = "Группы", icon = Icons.Outlined.Groups)
        }

    @Composable
    override fun Content() {
        GroupsScreen().Content()
    }
}

object ChannelsTab : Tab {
    private fun readResolve(): Any = ChannelsTab

    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 2u, title = "Каналы", icon = Icons.Outlined.Campaign)
        }

    @Composable
    override fun Content() {
        ChannelsScreen().Content()
    }
}

object ProfileTab : Tab {
    private fun readResolve(): Any = ProfileTab

    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 3u, title = "Профиль", icon = Icons.Outlined.Person)
        }

    @Composable
    override fun Content() {
        ProfileScreen().Content()
    }
}