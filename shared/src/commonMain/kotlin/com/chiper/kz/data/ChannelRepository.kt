package com.chiper.kz.data

import com.chiper.kz.model.Channel

class ChannelRepository {
    companion object {
        val demoChannels = listOf(
            Channel(
                id = "channel1",
                name = "Telegram News",
                avatarUrl = "",
                description = "Официальный канал новостей Telegram",
                subscribersCount = 8500000,
                lastPost = "Telegram 11.0: Истории, видеосообщения и реакции",
                lastPostTime = System.currentTimeMillis() - 3600000,
                unreadCount = 0,
                isVerified = true
            ),
            Channel(
                id = "channel2",
                name = "Android Developers Blog",
                avatarUrl = "",
                description = "Официальный блог Android разработчиков",
                subscribersCount = 1200000,
                lastPost = "Android 15 Beta 2 доступен для тестирования",
                lastPostTime = System.currentTimeMillis() - 7200000,
                unreadCount = 1,
                isVerified = true
            ),
            Channel(
                id = "channel3",
                name = "Jetpack Compose Updates",
                avatarUrl = "",
                description = "Все обновления и новости Jetpack Compose",
                subscribersCount = 340000,
                lastPost = "Compose 1.6.0: Performance improvements",
                lastPostTime = System.currentTimeMillis() - 14400000,
                unreadCount = 2,
                isVerified = true
            ),
            Channel(
                id = "channel4",
                name = "Kotlin Weekly",
                avatarUrl = "",
                description = "Еженедельная подборка новостей Kotlin",
                subscribersCount = 180000,
                lastPost = "Kotlin 2.3.20: Новые фичи и исправления",
                lastPostTime = System.currentTimeMillis() - 86400000,
                unreadCount = 0,
                isVerified = false
            ),
            Channel(
                id = "channel5",
                name = "Chiper Official",
                avatarUrl = "",
                description = "Официальный канал Chiper Messenger",
                subscribersCount = 45000,
                lastPost = "Выпуск версии 1.0: Glass Design System!",
                lastPostTime = System.currentTimeMillis() - 259200000,
                unreadCount = 0,
                isVerified = true
            ),
            Channel(
                id = "channel6",
                name = "Mobile Dev Daily",
                avatarUrl = "",
                description = "Ежедневные новости мобильной разработки",
                subscribersCount = 560000,
                lastPost = "iOS 18: Что нового для разработчиков",
                lastPostTime = System.currentTimeMillis() - 10800000,
                unreadCount = 3,
                isVerified = false
            ),
            Channel(
                id = "channel7",
                name = "Open Source Digest",
                avatarUrl = "",
                description = "Лучшие open source проекты недели",
                subscribersCount = 230000,
                lastPost = "Топ-10 библиотек для Compose в 2024",
                lastPostTime = System.currentTimeMillis() - 432000000,
                unreadCount = 0,
                isVerified = false
            ),
            Channel(
                id = "channel8",
                name = "Tech Memes",
                avatarUrl = "",
                description = "Мемы для разработчиков",
                subscribersCount = 980000,
                lastPost = "Когда код работает, но ты не знаешь почему",
                lastPostTime = System.currentTimeMillis() - 1800000,
                unreadCount = 5,
                isVerified = false
            )
        )
    }
}