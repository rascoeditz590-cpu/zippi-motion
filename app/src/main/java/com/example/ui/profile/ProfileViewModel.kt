package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userPreferencesRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile("guest", "Guest Creator", "guest@zippimotion.app", isGuest = true)
        )

    val isProxyEnabled: StateFlow<Boolean> = userPreferencesRepository.isProxyEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isSnappingEnabled: StateFlow<Boolean> = userPreferencesRepository.isSnappingEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun toggleProxy(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setProxyEnabled(enabled)
        }
    }

    fun toggleSnapping(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setSnappingEnabled(enabled)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.setUser("guest", "Guest Creator", "guest@zippimotion.app", isGuest = true)
            onComplete()
        }
    }
}
