package com.chiper.kz.di

import com.chiper.kz.data.AuthRepository
import com.chiper.kz.data.ChatRepository
import com.chiper.kz.data.GroupRepository
import com.chiper.kz.data.ChannelRepository
import com.chiper.kz.screens.auth.AuthViewModel
import com.chiper.kz.screens.channels.ChannelsViewModel
import com.chiper.kz.screens.chat.ChatViewModel
import com.chiper.kz.screens.chatlist.ChatListViewModel
import com.chiper.kz.screens.groups.GroupsViewModel
import com.chiper.kz.screens.profile.ProfileViewModel
import com.chiper.kz.screens.settings.SettingsViewModel
import com.chiper.kz.screens.security.SecuritySettingsViewModel
import com.chiper.kz.screens.notifications.NotificationsSettingsViewModel
import com.chiper.kz.theme.ThemeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // Repositories
    singleOf(::AuthRepository)
    singleOf(::ChatRepository)
    singleOf(::GroupRepository)
    singleOf(::ChannelRepository)

    // ViewModels
    factoryOf(::AuthViewModel)
    factoryOf(::ChatListViewModel)
    factoryOf(::ChatViewModel)
    factoryOf(::GroupsViewModel)
    factoryOf(::ChannelsViewModel)
    factoryOf(::ProfileViewModel)
    factoryOf(::SettingsViewModel)
    factoryOf(::ThemeViewModel)
    factoryOf(::SecuritySettingsViewModel)
    factoryOf(::NotificationsSettingsViewModel)
}