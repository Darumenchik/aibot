package com.chiper.kz.data

import com.chiper.kz.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val demoUsers = mutableMapOf(
        "user1@chiper.kz" to User(
            id = "user1",
            name = "Алексей Петров",
            email = "user1@chiper.kz",
            avatarUrl = "",
            isOnline = true,
            bio = "Android разработчик"
        ),
        "user2@chiper.kz" to User(
            id = "user2",
            name = "Мария Иванова",
            email = "user2@chiper.kz",
            avatarUrl = "",
            isOnline = true,
            bio = "Дизайнер интерфейсов"
        )
    )

    suspend fun login(email: String, password: String): Result<User> {
        kotlinx.coroutines.delay(1000)
        val user = demoUsers[email]
        return if (user != null && password.length >= 6) {
            _currentUser.value = user
            Result.success(user)
        } else if (password.length < 6) {
            Result.failure(Exception("Пароль должен содержать минимум 6 символов"))
        } else {
            Result.failure(Exception("Пользователь не найден"))
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        kotlinx.coroutines.delay(1500)
        if (demoUsers.containsKey(email)) {
            return Result.failure(Exception("Пользователь с таким email уже существует"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Пароль должен содержать минимум 6 символов"))
        }
        if (name.isBlank()) {
            return Result.failure(Exception("Введите имя"))
        }
        val user = User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            avatarUrl = "",
            isOnline = true,
            bio = ""
        )
        demoUsers[email] = user
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun loginWithGoogle(): Result<User> {
        kotlinx.coroutines.delay(1500)
        val user = User(
            id = "google_user_${System.currentTimeMillis()}",
            name = "Google User",
            email = "google@chiper.kz",
            avatarUrl = "",
            isOnline = true,
            bio = ""
        )
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun logout() {
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null
}
