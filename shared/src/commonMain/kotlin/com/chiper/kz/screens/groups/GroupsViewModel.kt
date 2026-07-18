package com.chiper.kz.screens.groups

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.chiper.kz.data.GroupRepository
import com.chiper.kz.model.Group
import kotlinx.coroutines.flow.StateFlow

class GroupsViewModel(
    private val groupRepository: GroupRepository
) : ScreenModel {

    val groups: StateFlow<List<Group>> = groupRepository.groups

    var uiState by mutableStateOf(GroupsUiState())
        private set

    fun showCreateGroupDialog() {
        // TODO: Show create group dialog
    }
}

data class GroupsUiState(
    val groups: List<Group> = GroupRepository.demoGroups,
    val isLoading: Boolean = false
)