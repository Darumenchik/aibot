package com.chiper.kz.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val isSentByMe: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val replyTo: String? = null,
    val replyPreview: String? = null,
    val reactions: Map<String, Int> = emptyMap(),
    val isEdited: Boolean = false,
    val mediaUrl: String? = null,
    val voiceDuration: Int = 0
)

@Serializable
enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    VOICE,
    VIDEO,
    STICKER
}

@Serializable
data class Reaction(
    val emoji: String,
    val count: Int,
    val hasReacted: Boolean,
    val users: List<String> = emptyList()
)