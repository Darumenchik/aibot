package com.chiper.kz.data

import com.chiper.kz.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GroupRepository {

    private val _groups = MutableStateFlow(demoGroups)
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    companion object {
        val demoGroups = listOf(
            Group(
                id = "group1",
                name = "Android Developers RU",
                avatarUrl = "",
                lastMessage = "Иван: Новый релиз Compose 1.6!",
                lastMessageTime = System.currentTimeMillis() - 120000,
                unreadCount = 5,
                isOnline = true,
                companionId = "android_ru",
                membersCount = 45600,
                description = "Самое большое русскоязычное сообщество Android разработчиков",
                isPublic = true
            ),
            Group(
                id = "group2",
                name = "Kotlin Multiplatform",
                avatarUrl = "",
                lastMessage = "Анна: Помогите с настройкой iOS таргета",
                lastMessageTime = System.currentTimeMillis() - 3600000,
                unreadCount = 0,
                isOnline = false,
                companionId = "kmp_chat",
                membersCount = 12300,
                description = "Обсуждение KMP: общие проблемы, библиотеки, лучшие практики",
                isPublic = true
            ),
            Group(
                id = "group3",
                name = "Jetpack Compose Chat",
                avatarUrl = "",
                lastMessage = "Петр: Кто пробовал новый LazyLayout?",
                lastMessageTime = System.currentTimeMillis() - 7200000,
                unreadCount = 2,
                isOnline = true,
                companionId = "compose_chat",
                membersCount = 28900,
                description = "Чат про Jetpack Compose: анимации, лейауты, перфоманс",
                isPublic = true
            ),
            Group(
                id = "group4",
                name = "Chiper Team",
                avatarUrl = "",
                lastMessage = "Ты: Отлично, пушим релиз в пятницу!",
                lastMessageTime = System.currentTimeMillis() - 14400000,
                unreadCount = 0,
                isOnline = true,
                companionId = "chiper_team",
                membersCount = 12,
                description = "Внутренний чат команды разработки Chiper",
                isPublic = false
            ),
            Group(
                id = "group5",
                name = "Flutter vs Compose Wars",
                avatarUrl = "",
                lastMessage = "Елена: Compose выигрывает в DX",
                lastMessageTime = System.currentTimeMillis() - 86400000,
                unreadCount = 3,
                isOnline = false,
                companionId = "flutter_vs_compose",
                membersCount = 23400,
                description = "Споры, бенчмарки, мнения разработчиков",
                isPublic = true
            ),
            Group(
                id = "group6",
                name = "Open Source Contributors",
                avatarUrl = "",
                lastMessage = "Максим: Приняли PR в kotlinx.coroutines",
                lastMessageTime = System.currentTimeMillis() - 172800000,
                unreadCount = 0,
                isOnline = false,
                companionId = "oss_contributors",
                membersCount = 15600,
                description = "Контрибьютеры open source проектов",
                isPublic = true
            ),
            Group(
                id = "group7",
                name = "Mobile Dev Jobs",
                avatarUrl = "",
                lastMessage = "HR: Ищем Senior Android в Яндекс",
                lastMessageTime = System.currentTimeMillis() - 259200000,
                unreadCount = 0,
                isOnline = false,
                companionId = "mobile_jobs",
                membersCount = 67800,
                description = "Вакансии для мобильных разработчиков",
                isPublic = true
            ),
            Group(
                id = "group8",
                name = "Architecture Patterns",
                avatarUrl = "",
                lastMessage = "Дмитрий: MVI vs MVVM в 2024",
                lastMessageTime = System.currentTimeMillis() - 432000000,
                unreadCount = 1,
                isOnline = false,
                companionId = "arch_patterns",
                membersCount = 34500,
                description = "Архитектура приложений: MVI, MVVM, Clean, Redux",
                isPublic = true
            )
        )
    }
}