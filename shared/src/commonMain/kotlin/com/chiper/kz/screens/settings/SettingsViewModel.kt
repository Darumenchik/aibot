package com.chiper.kz.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.chiper.kz.data.AuthRepository
import com.chiper.kz.theme.ChatDensity

class SettingsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(SettingsUiState())
        private set

    fun setChatDensity(density: ChatDensity) {
        uiState = uiState.copy(chatDensity = density)
    }

    fun setFontScale(scale: Float) {
        uiState = uiState.copy(fontScale = scale)
    }

    fun setHighContrast(enabled: Boolean) {
        uiState = uiState.copy(highContrast = enabled)
    }

    fun setLandscapeMode(enabled: Boolean) {
        uiState = uiState.copy(landscapeMode = enabled)
    }

    fun setRTLMode(enabled: Boolean) {
        uiState = uiState.copy(rtlMode = enabled)
    }

    fun setCurrentTheme(theme: com.chiper.kz.theme.AppTheme) {
        uiState = uiState.copy(currentTheme = theme)
    }
}

data class SettingsUiState(
    val chatDensity: ChatDensity = ChatDensity.Standard,
    val fontScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val landscapeMode: Boolean = false,
    val rtlMode: Boolean = false,
    val currentTheme: com.chiper.kz.theme.AppTheme = com.chiper.kz.theme.AppTheme.Default,
    val userName: String = "User"
)