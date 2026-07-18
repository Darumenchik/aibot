package com.chiper.kz.model

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val description: String = "",
    val subscribersCount: Int = 0,
    val lastPost: String = "",
    val lastPostTime: Long = 0L,
    val unreadCount: Int = 0,
    val isVerified: Boolean = false,
    val isMuted: Boolean = false
)