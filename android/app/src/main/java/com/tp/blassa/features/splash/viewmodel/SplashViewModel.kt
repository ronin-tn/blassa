package com.tp.blassa.features.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashEvent {
    data object NavigateToOnboarding : SplashEvent()
    data object NavigateToLogin : SplashEvent()
    data object NavigateToCompleteProfile : SplashEvent()
    data object NavigateToDashboard : SplashEvent()
}

class SplashViewModel : ViewModel() {

    private val _event = MutableStateFlow<SplashEvent?>(null)
    val event: StateFlow<SplashEvent?> = _event.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (!TokenManager.isLoggedIn()) {
                if (TokenManager.hasCompletedOnboarding()) {
                    _event.value = SplashEvent.NavigateToDashboard
                } else {
                    _event.value = SplashEvent.NavigateToOnboarding
                }
                return@launch
            }

            try {
                val profile = RetrofitClient.dashboardApiService.getUserProfile()
                val isProfileComplete =
                        !profile.phoneNumber.isNullOrBlank() &&
                                !profile.dateOfBirth.isNullOrBlank() &&
                                !profile.gender.isNullOrBlank()

                if (isProfileComplete) {
                    _event.value = SplashEvent.NavigateToDashboard
                } else {
                    _event.value = SplashEvent.NavigateToCompleteProfile
                }
            } catch (e: Exception) {
                TokenManager.clearTokens()
                _event.value =
                        if (TokenManager.hasCompletedOnboarding()) {
                            SplashEvent.NavigateToDashboard
                        } else {
                            SplashEvent.NavigateToLogin
                        }
            }
        }
    }
}
