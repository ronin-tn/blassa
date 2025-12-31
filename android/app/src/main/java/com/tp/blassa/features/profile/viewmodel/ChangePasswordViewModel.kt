package com.tp.blassa.features.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.ChangePasswordRequest
import com.tp.blassa.core.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null,
        val isPasswordChanged: Boolean = false
)

class ChangePasswordViewModel : ViewModel() {

    companion object {
        private const val TAG = "ChangePasswordViewModel"
        private const val MIN_PASSWORD_LENGTH = 8
    }

    private val apiService = RetrofitClient.dashboardApiService

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(value: String) {
        _uiState.update { it.copy(currentPassword = value, error = null) }
    }

    fun updateNewPassword(value: String) {
        _uiState.update { it.copy(newPassword = value, error = null) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value, error = null) }
    }

    fun changePassword() {
        val state = _uiState.value

        if (state.currentPassword.isBlank()) {
            _uiState.update { it.copy(error = "Veuillez entrer votre mot de passe actuel") }
            return
        }

        if (state.newPassword.length < MIN_PASSWORD_LENGTH) {
            _uiState.update {
                it.copy(
                        error =
                                "Le nouveau mot de passe doit contenir au moins $MIN_PASSWORD_LENGTH caractères"
                )
            }
            return
        }

        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(error = "Les mots de passe ne correspondent pas") }
            return
        }

        if (state.currentPassword == state.newPassword) {
            _uiState.update {
                it.copy(error = "Le nouveau mot de passe doit être différent de l'actuel")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val request =
                        ChangePasswordRequest(
                                currentPassword = state.currentPassword,
                                newPassword = state.newPassword
                        )

                apiService.changePassword(request)

                _uiState.update {
                    it.copy(
                            isLoading = false,
                            successMessage = "Mot de passe modifié avec succès",
                            isPasswordChanged = true,
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = ""
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to change password: ${e.message}", e)
                val errorMessage =
                        when {
                            e.message?.contains("401") == true -> "Mot de passe actuel incorrect"
                            e.message?.contains("400") == true -> "Requête invalide"
                            else -> "Erreur lors du changement de mot de passe"
                        }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
