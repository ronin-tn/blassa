package isimm.ing1.mobile.presentation.screens.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    var uiState by mutableStateOf(ForgotPasswordUiState())
        private set
    
    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email, emailError = "")
    }
    
    fun validateAndSendLink(onSuccess: (String) -> Unit) {
        var emailError = ""
        
        if (uiState.email.isBlank()) {
            emailError = "Un email est requis"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            emailError = "Email invalide"
        }
        
        uiState = uiState.copy(emailError = emailError)
        
        if (emailError.isEmpty()) {
            // TODO: Implement actual password reset with Firebase Auth
            uiState = uiState.copy(isLoading = true)
            onSuccess(uiState.email)
        }
    }
}
