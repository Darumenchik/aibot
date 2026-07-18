package com.chiper.kz.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.data.AuthRepository
import com.chiper.kz.model.User
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    val currentUser: User? get() = authRepository.currentUser.value

    var isLoggedOut by mutableStateOf(false)
        private set

    fun logout() {
        screenModelScope.launch {
            authRepository.logout()
            isLoggedOut = true
        }
    }
}