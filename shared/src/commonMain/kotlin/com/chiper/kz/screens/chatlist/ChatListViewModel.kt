package com.chiper.kz.screens.chatlist

import cafe.adriel.voyager.core.model.ScreenModel
import com.chiper.kz.data.ChatRepository
import com.chiper.kz.model.Chat
import kotlinx.coroutines.flow.StateFlow

class ChatListViewModel(
    private val chatRepository: ChatRepository
) : ScreenModel {

    val chats: StateFlow<List<Chat>> = chatRepository.chats

    fun markAsRead(chatId: String) {
        chatRepository.markChatAsRead(chatId)
    }
}