package com.tp.blassa.features.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.network.ChangeEmailRequest
import com.tp.blassa.core.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangeEmailUiState(
        val currentEmail: String = "",
        val newEmail: String = "",
        val password: String = "",
        val isLoading: Boolean = true,
        val isSubmitting: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
)

class ChangeEmailViewModel : ViewModel() {

    companion object {
        private const val TAG = "ChangeEmailViewModel"
    }

    private val apiService = RetrofitClient.dashboardApiService

    private val _uiState = MutableStateFlow(ChangeEmailUiState())
    val uiState: StateFlow<ChangeEmailUiState> = _uiState.asStateFlow()

    init {
        loadCurrentEmail()
    }

    private fun loadCurrentEmail() {
        viewModelScope.launch {
            try {
                val profile = apiService.getUserProfile()
                _uiState.update { it.copy(isLoading = false, currentEmail = profile.email) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load current email: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Impossible de charger les informations")
                }
            }
        }
    }

    fun updateNewEmail(value: String) {
        _uiState.update { it.copy(newEmail = value, error = null) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun changeEmail() {
        val state = _uiState.value

        // Validation
        if (state.newEmail.isBlank()) {
            _uiState.update { it.copy(error = "Veuillez entrer un nouvel email") }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.newEmail).matches()) {
            _uiState.update { it.copy(error = "Format d'email invalide") }
            return
        }

        if (state.newEmail.equals(state.currentEmail, ignoreCase = true)) {
            _uiState.update {
                it.copy(error = "Le nouvel email doit être différent de l'email actuel")
            }
            return
        }

        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Veuillez entrer votre mot de passe") }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, error = null, successMessage = null) }

        viewModelScope.launch {
            try {
                val request =
                        ChangeEmailRequest(newEmail = state.newEmail, password = state.password)

                val response = apiService.changeEmail(request)

                // Save the new access token to keep the user logged in
                TokenManager.saveTokens(response.accessToken, null)

                _uiState.update {
                    it.copy(
                            isSubmitting = false,
                            currentEmail = response.profile.email,
                            successMessage =
                                    "Email modifié avec succès. Veuillez vérifier votre nouvelle adresse email.",
                            newEmail = "",
                            password = ""
                    )
                }
            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "Failed to change email: ${e.message}", e)
                val errorMessage =
                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            if (errorBody?.contains("mot de passe", ignoreCase = true) == true) {
                                "Le mot de passe est incorrect"
                            } else if (errorBody?.contains("déjà utilisée", ignoreCase = true) ==
                                            true
                            ) {
                                "Cette adresse email est déjà utilisée"
                            } else {
                                "Erreur lors du changement d'email"
                            }
                        } catch (_: Exception) {
                            "Erreur lors du changement d'email"
                        }
                _uiState.update { it.copy(isSubmitting = false, error = errorMessage) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to change email: ${e.message}", e)
                _uiState.update { it.copy(isSubmitting = false, error = "Erreur de connexion") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
