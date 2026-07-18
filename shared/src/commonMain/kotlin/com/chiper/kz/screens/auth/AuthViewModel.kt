package com.chiper.kz.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.data.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun onNameChanged(name: String) {
        uiState = uiState.copy(name = name, error = null)
    }

    fun onTabChanged(isLogin: Boolean) {
        uiState = uiState.copy(isLoginTab = isLogin, error = null)
    }

    fun onSubmit() {
        if (uiState.isLoading) return

        val state = uiState
        if (state.email.isBlank()) {
            uiState = state.copy(error = "Введите email")
            return
        }
        if (state.password.length < 6) {
            uiState = state.copy(error = "Минимум 6 символов в пароле")
            return
        }

        screenModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = if (state.isLoginTab) {
                authRepository.login(state.email, state.password)
            } else {
                if (state.name.isBlank()) {
                    uiState = uiState.copy(isLoading = false, error = "Введите имя")
                    return@launch
                }
                authRepository.register(state.name, state.email, state.password)
            }
            result.fold(
                onSuccess = {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { e ->
                    uiState = uiState.copy(isLoading = false, error = e.message ?: "Ошибка")
                }
            )
        }
    }

    fun onGoogleLogin() {
        if (uiState.isLoading) return
        screenModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = authRepository.loginWithGoogle()
            result.fold(
                onSuccess = {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { e ->
                    uiState = uiState.copy(isLoading = false, error = e.message ?: "Ошибка")
                }
            )
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoginTab: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)