package com.chiper.kz.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val companionId: String = "",
    val isTyping: Boolean = false
)
