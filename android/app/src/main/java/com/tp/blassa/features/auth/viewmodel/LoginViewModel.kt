package com.tp.blassa.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.network.LoginRequest
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.parseErrorMessage
import java.net.UnknownHostException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val passwordVisible: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val apiError: String? = null,
        val isLoading: Boolean = false,
        val isGoogleLoading: Boolean = false,
        val loginSuccess: Boolean = false,
        val isProfileIncomplete: Boolean = false,
        val snackbarMessage: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, apiError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, apiError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun validateEmail(): Boolean {
        val email = _uiState.value.email
        val emailError =
                when {
                    email.isBlank() -> "Email requis"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                            "Email invalide"
                    else -> null
                }
        _uiState.update { it.copy(emailError = emailError) }
        return emailError == null
    }

    private fun validatePassword(): Boolean {
        val password = _uiState.value.password
        val passwordError = if (password.isBlank()) "Mot de passe requis" else null
        _uiState.update { it.copy(passwordError = passwordError) }
        return passwordError == null
    }

    fun login() {
        if (!validateEmail() || !validatePassword()) return

        _uiState.update { it.copy(isLoading = true, apiError = null) }

        viewModelScope.launch {
            try {
                val response =
                        RetrofitClient.authApiService.login(
                                LoginRequest(
                                        email = _uiState.value.email,
                                        password = _uiState.value.password
                                )
                        )

                when (response.status) {
                    "SUCCESS" -> {
                        response.accessToken?.let { token ->
                            TokenManager.saveTokens(token, response.refreshToken)
                            val emailToSave = response.email ?: _uiState.value.email
                            TokenManager.saveUserEmail(emailToSave)

                            try {
                                val profile = RetrofitClient.dashboardApiService.getUserProfile()
                                if (profile.phoneNumber.isNullOrBlank() ||
                                                profile.dateOfBirth.isNullOrBlank() ||
                                                profile.gender.isNullOrBlank()
                                ) {
                                    _uiState.update {
                                        it.copy(
                                                isLoading = false,
                                                isProfileIncomplete = true,
                                                loginSuccess = true
                                        )
                                    }
                                } else {
                                    _uiState.update {
                                        it.copy(
                                                isLoading = false,
                                                loginSuccess = true,
                                                isProfileIncomplete = false,
                                                snackbarMessage = "Connexion réussie!"
                                        )
                                    }
                                }
                            } catch (e: Exception) {

                                _uiState.update {
                                    it.copy(
                                            isLoading = false,
                                            loginSuccess = true,
                                            snackbarMessage = "Connexion réussie!"
                                    )
                                }
                            }
                        }
                    }
                    "EMAIL_NOT_VERIFIED" -> {
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    apiError =
                                            "Veuillez vérifier votre email avant de vous connecter.",
                                    snackbarMessage = "Email non vérifié"
                            )
                        }
                    }
                    else -> {
                        val message = response.message ?: "Erreur de connexion"
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    apiError = message,
                                    snackbarMessage = message
                            )
                        }
                    }
                }
            } catch (e: retrofit2.HttpException) {
                val errorMessage = e.parseErrorMessage()
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            apiError = errorMessage,
                            snackbarMessage = errorMessage
                    )
                }
            } catch (e: UnknownHostException) {
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            apiError = "Impossible de se connecter au serveur",
                            snackbarMessage = "Impossible de se connecter au serveur"
                    )
                }
            } catch (e: java.net.SocketTimeoutException) {
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            apiError = "Le serveur ne répond pas",
                            snackbarMessage = "Le serveur ne répond pas"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            apiError = "Erreur réseau. Vérifiez votre connexion.",
                            snackbarMessage = "Erreur réseau. Vérifiez votre connexion."
                    )
                }
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _uiState.update { it.copy(isGoogleLoading = true, apiError = null) }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.authApiService.googleAuth(mapOf("idToken" to idToken))

                when (response.status) {
                    "SUCCESS" -> {
                        response.accessToken?.let { token ->
                            TokenManager.saveTokens(token, response.refreshToken)
                            val emailToSave = response.email ?: _uiState.value.email
                            if (emailToSave.isNotBlank()) {
                                TokenManager.saveUserEmail(emailToSave)
                            }

                            try {
                                val profile = RetrofitClient.dashboardApiService.getUserProfile()
                                if (profile.phoneNumber.isNullOrBlank() ||
                                                profile.dateOfBirth.isNullOrBlank() ||
                                                profile.gender.isNullOrBlank()
                                ) {
                                    _uiState.update {
                                        it.copy(
                                                isGoogleLoading = false,
                                                loginSuccess = true,
                                                isProfileIncomplete = true
                                        )
                                    }
                                } else {
                                    _uiState.update {
                                        it.copy(
                                                isGoogleLoading = false,
                                                loginSuccess = true,
                                                isProfileIncomplete = false,
                                                snackbarMessage = "Connexion réussie!"
                                        )
                                    }
                                }
                            } catch (e: Exception) {

                                _uiState.update {
                                    it.copy(
                                            isGoogleLoading = false,
                                            loginSuccess = true,
                                            snackbarMessage = "Connexion réussie!"
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        val message = response.message ?: "Échec de connexion Google"
                        _uiState.update {
                            it.copy(
                                    isGoogleLoading = false,
                                    apiError = message,
                                    snackbarMessage = message
                            )
                        }
                    }
                }
            } catch (e: retrofit2.HttpException) {
                val errorMessage = e.parseErrorMessage()
                _uiState.update {
                    it.copy(
                            isGoogleLoading = false,
                            apiError = errorMessage,
                            snackbarMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                            isGoogleLoading = false,
                            apiError = "Erreur Google Sign-In: ${e.message}",
                            snackbarMessage = "Erreur Google Sign-In"
                    )
                }
            }
        }
    }

    fun onGoogleSignInCancelled() {
        _uiState.update { it.copy(isGoogleLoading = false) }
    }
}
