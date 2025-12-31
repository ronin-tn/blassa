package com.tp.blassa.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileMenuUiState(
        val userProfile: UserProfile? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isGuest: Boolean = false
)

class ProfileMenuViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileMenuUiState())
    val uiState: StateFlow<ProfileMenuUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        if (!TokenManager.isLoggedIn()) {
            _uiState.update { it.copy(isGuest = true, isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = RetrofitClient.dashboardApiService.getUserProfile()
                _uiState.update { it.copy(userProfile = profile, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Impossible de charger le profil", isLoading = false)
                }
            }
        }
    }
}
