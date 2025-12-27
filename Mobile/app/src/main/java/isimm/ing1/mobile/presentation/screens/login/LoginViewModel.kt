package isimm.ing1.mobile.presentation.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * UI state for the Login screen.
 * 
 * @property email User's email input
 * @property password User's password input  
 * @property emailError Validation error message for email field
 * @property passwordError Validation error message for password field
 * @property isLoading True when authentication is in progress
 * @property errorMessage General error message from authentication failure
 * 
 * TODO (Backend): Migrate to sealed interface UiState pattern per architecture.md:
 * sealed interface LoginUiState {
 *     object Idle : LoginUiState
 *     object Loading : LoginUiState
 *     data class Success(val user: User) : LoginUiState
 *     data class Error(val message: String) : LoginUiState
 * }
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Login screen.
 * Handles form validation and authentication flow.
 * 
 * ## Architecture Notes (per architecture.md):
 * - Currently uses mutableStateOf for simplicity
 * - TODO (Backend): Migrate to StateFlow for production
 * - TODO (Backend): Inject LoginUseCase via Hilt constructor
 * - TODO (Backend): Call Firebase Auth in validateAndLogin()
 * 
 * ## Example with UseCase:
 * ```kotlin
 * class LoginViewModel @Inject constructor(
 *     private val loginUseCase: LoginUseCase
 * ) : ViewModel() {
 *     private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
 *     val state: StateFlow<LoginUiState> = _state.asStateFlow()
 * }
 * ```
 */
class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set
    
    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email, emailError = "")
    }
    
    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password, passwordError = "")
    }
    
    fun validateAndLogin(onSuccess: (String) -> Unit) {
        var isValid = true
        var emailError = ""
        var passwordError = ""
        
        if (uiState.email.isBlank()) {
            emailError = "Un email est requis"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            emailError = "Email invalide"
            isValid = false
        }
        
        if (uiState.password.isBlank()) {
            passwordError = "Le mot de passe est requis"
            isValid = false
        } else if (uiState.password.length < 6) {
            passwordError = "Le mot de passe doit contenir au moins 6 caractères"
            isValid = false
        }
        
        uiState = uiState.copy(emailError = emailError, passwordError = passwordError)
        
        if (isValid) {
            // TODO: Implement actual login with Firebase Auth
            uiState = uiState.copy(isLoading = true)
            // For now, simulate success
            onSuccess(uiState.email)
        }
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}
