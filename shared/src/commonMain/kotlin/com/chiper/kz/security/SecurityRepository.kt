package com.chiper.kz.security

import androidx.datastore.core.DataStore
import androidx.datastore.core.preferences.mutations
import androidx.datastore.core.preferences.preferencesKey
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SecurityRepository(private val dataStore: DataStore<Preferences>) {

    private val PASSCODE_KEY = preferencesKey<String>("passcode")
    private val PASSCODE_ENABLED_KEY = preferencesKey<Boolean>("passcode_enabled")
    private val BIOMETRIC_ENABLED_KEY = preferencesKey<Boolean>("biometric_enabled")
    private val FAILED_ATTEMPTS_KEY = preferencesKey<Int>("failed_attempts")
    private val LOCKOUT_UNTIL_KEY = preferencesKey<Long>("lockout_until")
    private val AUTO_LOCK_TIMEOUT_KEY = preferencesKey<Long>("auto_lock_timeout")
    private val LAST_ACTIVE_KEY = preferencesKey<Long>("last_active")

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _passcodeEnabled = MutableStateFlow(false)
    val passcodeEnabled: StateFlow<Boolean> = _passcodeEnabled.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _autoLockTimeout = MutableStateFlow(300000L) // 5 min default
    val autoLockTimeout: StateFlow<Long> = _autoLockTimeout.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = dataStore.data.firstOrNull().await()
                prefs?.let {
                    _passcodeEnabled.value = it[PASSCODE_ENABLED_KEY] ?: false
                    _biometricEnabled.value = it[BIOMETRIC_ENABLED_KEY] ?: false
                    _autoLockTimeout.value = it[AUTO_LOCK_TIMEOUT_KEY] ?: 300000L
                }
            } catch (e: Exception) {
                // Use defaults
            }
        }
    }

    suspend fun setPasscode(passcode: String) {
        dataStore.edit { prefs ->
            prefs.mutations()
                .set(PASSCODE_KEY, passcode)
                .set(PASSCODE_ENABLED_KEY, true)
        }
        _passcodeEnabled.value = true
    }

    suspend fun removePasscode() {
        dataStore.edit { prefs ->
            prefs.mutations()
                .remove(PASSCODE_KEY)
                .set(PASSCODE_ENABLED_KEY, false)
        }
        _passcodeEnabled.value = false
    }

    suspend fun verifyPasscode(passcode: String): Boolean {
        val prefs = dataStore.data.firstOrNull().await()
        val stored = prefs?.get(PASSCODE_KEY) ?: return false
        return stored == passcode
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs.mutations().set(BIOMETRIC_ENABLED_KEY, enabled)
        }
        _biometricEnabled.value = enabled
    }

    suspend fun setAutoLockTimeout(timeout: Long) {
        dataStore.edit { prefs ->
            prefs.mutations().set(AUTO_LOCK_TIMEOUT_KEY, timeout)
        }
        _autoLockTimeout.value = timeout
    }

    fun recordFailedAttempt() {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = dataStore.data.firstOrNull().await()
            val attempts = (prefs?.get(FAILED_ATTEMPTS_KEY) ?: 0) + 1
            var lockoutUntil = 0L
            
            if (attempts >= 5) {
                lockoutUntil = System.currentTimeMillis() + (attempts - 4) * 60000 // 1min, 2min, 3min...
            }
            
            dataStore.edit { prefs ->
                prefs.mutations()
                    .set(FAILED_ATTEMPTS_KEY, attempts)
                    .set(LOCKOUT_UNTIL_KEY, lockoutUntil)
            }
        }
    }

    fun resetFailedAttempts() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { prefs ->
                prefs.mutations()
                    .set(FAILED_ATTEMPTS_KEY, 0)
                    .set(LOCKOUT_UNTIL_KEY, 0L)
            }
        }
    }

    fun isLockedOut(): Boolean {
        try {
            val prefs = dataStore.data.firstOrNull().await()
            val lockoutUntil = prefs?.get(LOCKOUT_UNTIL_KEY) ?: 0L
            return lockoutUntil > 0 && System.currentTimeMillis() < lockoutUntil
        } catch (e: Exception) {
            return false
        }
    }

    fun getLockoutRemaining(): Long {
        try {
            val prefs = dataStore.data.firstOrNull().await()
            val lockoutUntil = prefs?.get(LOCKOUT_UNTIL_KEY) ?: 0L
            return (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0)
        } catch (e: Exception) {
            return 0
        }
    }

    fun onAppResume() {
        val now = System.currentTimeMillis()
        try {
            val prefs = dataStore.data.firstOrNull().await()
            val lastActive = prefs?.get(LAST_ACTIVE_KEY) ?: 0L
            val timeout = prefs?.get(AUTO_LOCK_TIMEOUT_KEY) ?: 300000L
            
            if (now - lastActive > timeout && _passcodeEnabled.value) {
                _isLocked.value = true
            }
        } catch (e: Exception) {
            _isLocked.value = _passcodeEnabled.value
        }
        updateLastActive()
    }

    fun onAppPause() {
        updateLastActive()
    }

    private fun updateLastActive() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { prefs ->
                prefs.mutations().set(LAST_ACTIVE_KEY, System.currentTimeMillis())
            }
        }
    }

    fun lock() {
        _isLocked.value = true
    }

    fun unlock() {
        _isLocked.value = false
    }
}