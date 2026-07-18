package com.chiper.kz.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.chiper.kz.screens.onboarding.OnboardingScreen
import com.chiper.kz.screens.groups.GroupsScreen
import com.chiper.kz.screens.channels.ChannelsScreen

sealed class Screen {
    data object Splash : Screen()
    data object Onboarding : Screen()
    data object Auth : Screen()
    data object Main : Screen()
    data object ChatList : Screen()
    data class Chat(val chatId: String, val chatName: String, val avatarUrl: String) : Screen()
    data object Profile : Screen()
    data object Groups : Screen()
    data object Channels : Screen()
    data class GroupChat(val chatId: String, val chatName: String, val avatarUrl: String) : Screen()
    data class Channel(val channelId: String, val channelName: String, val avatarUrl: String) : Screen()
}

object ScreenKeys {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val MAIN = "main"
    const val CHAT_LIST = "chat_list"
    const val CHAT = "chat"
    const val PROFILE = "profile"
    const val GROUPS = "groups"
    const val CHANNELS = "channels"
    const val GROUP_CHAT = "group_chat"
    const val CHANNEL = "channel"
}