package com.chiper.kz.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ThemeViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow(settingsRepository.getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _appTheme = MutableStateFlow(settingsRepository.getAppTheme())
    val appTheme: StateFlow<AppTheme> = _appTheme.asStateFlow()

    private val _isAnimating = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean> = _isAnimating.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        settingsRepository.setThemeMode(mode)
    }

    fun setAppTheme(theme: AppTheme) {
        _isAnimating.value = true
        _appTheme.value = theme
        settingsRepository.setAppTheme(theme)
        
        viewModelScope.launch {
            delay(500)
            _isAnimating.value = false
        }
    }
}

sealed interface ThemeMode {
    data object Light : ThemeMode
    data object Dark : ThemeMode
    data object System : ThemeMode
}

class SettingsRepository(private val dataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>) {

    private val THEME_ID_KEY = androidx.datastore.core.preferences.preferencesKey<String>("theme_id")
    private val THEME_MODE_KEY = androidx.datastore.core.preferences.preferencesKey<String>("theme_mode")

    fun getAppTheme(): AppTheme {
        try {
            val prefs = dataStore.data.firstOrNull().await()
            val themeId = prefs?.get(THEME_ID_KEY) ?: AppTheme.Default.id
            return AppTheme.fromId(themeId)
        } catch (e: Exception) {
            return AppTheme.Default
        }
    }

    fun getThemeMode(): ThemeMode {
        try {
            val prefs = dataStore.data.firstOrNull().await()
            val modeStr = prefs?.get(THEME_MODE_KEY) ?: ThemeMode.System.name
            return ThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            return ThemeMode.System
        }
    }

    suspend fun setAppTheme(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs.mutations().set(THEME_ID_KEY, theme.id)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs.mutations().set(THEME_MODE_KEY, mode.name)
        }
    }
}