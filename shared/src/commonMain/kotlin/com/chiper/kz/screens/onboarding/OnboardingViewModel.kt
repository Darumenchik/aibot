package com.chiper.kz.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.data.AuthRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    fun next() {
        val nextPage = (uiState.currentPage + 1) % 4
        _uiState.value = _uiState.value.copy(currentPage = nextPage)
    }

    fun skip() {
        // handled by navigator
    }

    fun getStarted() {
        // handled by navigator
    }
}

data class OnboardingUiState(
    val currentPage: Int = 0
)

data class OnboardingPage(
    val imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val primaryColor: androidx.compose.ui.graphics.Color,
    val secondaryColor: androidx.compose.ui.graphics.Color
)