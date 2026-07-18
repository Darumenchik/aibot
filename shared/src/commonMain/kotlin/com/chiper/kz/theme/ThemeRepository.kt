package com.chiper.kz.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.preferences.mutations
import androidx.datastore.core.preferences.preferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.edit
import androidx.datastore.rxjava3.RxDataStore
import androidx.datastore.rxjava3.RxDataStoreBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// DataStore keys
private val THEME_ID_KEY = preferencesKey<String>("theme_id")
private val THEME_MODE_KEY = preferencesKey<String>("theme_mode")

class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    private val _currentTheme = MutableStateFlow(AppTheme.Default)
    val currentTheme: StateFlow<AppTheme> = _currentTheme
        .distinctUntilChanged()

    private val _themeMode = MutableStateFlow(ThemeMode.System)
    val themeMode: StateFlow<ThemeMode> = _themeMode
        .distinctUntilChanged()

    init {
        loadTheme()
    }

    private fun loadTheme() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val prefs = dataStore.data.firstOrNull().await()
                prefs?.let {
                    val themeId = it[THEME_ID_KEY] ?: AppTheme.Default.id
                    val modeStr = it[THEME_MODE_KEY] ?: ThemeMode.System.name
                    _currentTheme.value = AppTheme.fromId(themeId)
                    _themeMode.value = ThemeMode.valueOf(modeStr)
                }
            } catch (e: Exception) {
                _currentTheme.value = AppTheme.Default
                _themeMode.value = ThemeMode.System
            }
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        dataStore.edit { prefs ->
            prefs.mutations().set(THEME_ID_KEY, theme.id)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        dataStore.edit { prefs ->
            prefs.mutations().set(THEME_MODE_KEY, mode.name)
        }
    }
}

@Composable
fun rememberThemeRepository(): ThemeRepository {
    val dataStore = remember { createDataStore() }
    return remember { ThemeRepository(dataStore) }
}

private fun createDataStore(): DataStore<Preferences> {
    // In real app, this would use Context.createDataStore
    // For KMP, we use the platform-specific implementation
    return RxDataStoreBuilder("chiper_prefs").build()
}

val themeModule = module {
    singleOf { ThemeRepository(createDataStore()) }
}