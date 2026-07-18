package com.chiper.kz.screens.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.data.ChatRepository
import com.chiper.kz.model.Message
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val chatId: String
) : ScreenModel {

    val messages: StateFlow<List<Message>> = chatRepository.messages

    var inputText by mutableStateOf("")
        private set

    var isTyping by mutableStateOf(false)
        private set

    private var typingJob: Job? = null

    fun getMessagesForChat(): List<Message> {
        return chatRepository.getMessagesForChat(chatId)
    }

    fun onInputChanged(text: String) {
        inputText = text
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isEmpty()) return

        screenModelScope.launch {
            inputText = ""
            chatRepository.sendMessage(chatId, text)
            isTyping = true
            delay(2000)
            isTyping = false
        }
    }

    fun markAsRead() {
        chatRepository.markChatAsRead(chatId)
    }
}