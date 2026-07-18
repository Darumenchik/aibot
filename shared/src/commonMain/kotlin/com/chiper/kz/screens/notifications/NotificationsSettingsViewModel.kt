package com.chiper.kz.screens.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope

class NotificationsSettingsViewModel : ScreenModel {

    var uiState by mutableStateOf(NotificationsSettingsUiState())
        private set

    fun setNotificationsEnabled(enabled: Boolean) {
        uiState = uiState.copy(notificationsEnabled = enabled)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        uiState = uiState.copy(vibrationEnabled = enabled)
    }

    fun setPriorityEnabled(enabled: Boolean) {
        uiState = uiState.copy(priorityEnabled = enabled)
    }

    fun showSoundPicker() {
        uiState = uiState.copy(showSoundPicker = true)
    }

    fun selectSound(sound: String) {
        uiState = uiState.copy(selectedSound = sound, showSoundPicker = false)
    }

    fun showPerChatNotifications() {
        // Navigate
    }

    fun showDNDSettings() {
        // Navigate
    }
}

data class NotificationsSettingsUiState(
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val selectedSound: String = "Default",
    val vibrationEnabled: Boolean = true,
    val priorityEnabled: Boolean = true,
    val showSoundPicker: Boolean = false,
    val perChatNotifications: List<PerChatNotification> = listOf(
        PerChatNotification("Чат 1", true, "Default", true),
        PerChatNotification("Чат 2", false, "Custom", false),
        PerChatNotification("Чат 3", true, "Default", true)
    ),
    val dndEnabled: Boolean = false,
    val dndSchedule: String? = null
)

data class PerChatNotification(
    val name: String,
    val enabled: Boolean,
    val sound: String,
    val vibration: Boolean
)