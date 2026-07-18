package com.chiper.kz.screens.channels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.data.ChannelRepository
import com.chiper.kz.model.Channel
import kotlinx.coroutines.launch

class ChannelsViewModel(
    private val channelRepository: ChannelRepository
) : ScreenModel {

    var uiState by mutableStateOf(ChannelsUiState())
        private set

    fun showCreateChannelDialog() {
        // TODO: Show create channel dialog
    }

    fun showSearch() {
        // TODO: Show search
    }
}

data class ChannelsUiState(
    val channels: List<Channel> = ChannelRepository.demoChannels,
    val isLoading: Boolean = false
)