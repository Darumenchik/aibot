package com.chiper.kz.screens.security

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.security.SecurityRepository
import kotlinx.coroutines.launch

class SecuritySettingsViewModel(
    private val securityRepository: SecurityRepository
) : ScreenModel {

    var uiState by mutableStateOf(SecuritySettingsUiState())
        private set

    init {
        loadSettings()
    }

    private fun loadSettings() {
        uiState = SecuritySettingsUiState(
            passcodeEnabled = securityRepository.passcodeEnabled.value,
            biometricEnabled = securityRepository.biometricEnabled.value,
            autoLockTimeout = securityRepository.autoLockTimeout.value,
            twoFAEnabled = securityRepository.totpEnabled.value
        )
    }

    fun toggleBiometric(enabled: Boolean) {
        securityRepository.setBiometricEnabled(enabled)
        uiState = uiState.copy(biometricEnabled = enabled)
    }

    fun showAutoLockOptions() {
        uiState = uiState.copy(showAutoLockDialog = true)
    }

    fun setAutoLockTimeout(timeout: Long) {
        securityRepository.setAutoLockTimeout(timeout)
        uiState = uiState.copy(autoLockTimeout = timeout, showAutoLockDialog = false)
    }

    fun showSessions() {
        // Navigate to sessions screen
    }

    fun lockApp() {
        securityRepository.lock()
    }
}

data class SecuritySettingsUiState(
    val passcodeEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoLockTimeout: Long = 300000L,
    val twoFAEnabled: Boolean = false,
    val showAutoLockDialog: Boolean = false
)