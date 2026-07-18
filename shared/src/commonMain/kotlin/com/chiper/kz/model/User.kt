package com.chiper.kz.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val bio: String = ""
)
