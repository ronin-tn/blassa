package isimm.ing1.mobile.presentation.screens.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordError: String = "",
    val confirmError: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ResetPasswordViewModel : ViewModel() {
    var uiState by mutableStateOf(ResetPasswordUiState())
        private set
    
    fun updateNewPassword(password: String) {
        uiState = uiState.copy(newPassword = password, passwordError = "")
    }
    
    fun updateConfirmPassword(password: String) {
        uiState = uiState.copy(confirmPassword = password, confirmError = "")
    }
    
    fun getPasswordStrength(): Int {
        val password = uiState.newPassword
        return when {
            password.length < 6 -> 1
            password.length < 8 -> 2
            password.any { it.isUpperCase() } && password.any { it.isDigit() } -> 4
            else -> 3
        }
    }
    
    fun validateAndReset(onSuccess: () -> Unit) {
        var passwordError = ""
        var confirmError = ""
        
        if (uiState.newPassword.length < 6) {
            passwordError = "Le mot de passe doit contenir au moins 6 caractères"
        }
        
        if (uiState.confirmPassword.isBlank()) {
            confirmError = "Confirmation requise"
        } else if (uiState.confirmPassword != uiState.newPassword) {
            confirmError = "Les mots de passe ne correspondent pas"
        }
        
        uiState = uiState.copy(passwordError = passwordError, confirmError = confirmError)
        
        if (passwordError.isEmpty() && confirmError.isEmpty()) {
            // TODO: Implement actual password reset with Firebase Auth
            uiState = uiState.copy(isLoading = true)
            onSuccess()
        }
    }
}
