package isimm.ing1.mobile.presentation.screens.verification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class EmailVerificationUiState(
    val isResending: Boolean = false,
    val resendSuccess: Boolean = false,
    val errorMessage: String? = null
)

class EmailVerificationViewModel : ViewModel() {
    var uiState by mutableStateOf(EmailVerificationUiState())
        private set
    
    fun resendVerificationEmail(email: String, onSuccess: () -> Unit) {
        uiState = uiState.copy(isResending = true)
        // TODO: Implement actual resend with Firebase Auth
        // Simulate success
        uiState = uiState.copy(isResending = false, resendSuccess = true)
        onSuccess()
    }
    
    fun checkVerificationStatus(onVerified: () -> Unit) {
        // TODO: Check if email is verified with Firebase Auth
        // For now, this would be called after user clicks the email link
    }
    
    fun clearMessages() {
        uiState = uiState.copy(resendSuccess = false, errorMessage = null)
    }
}
