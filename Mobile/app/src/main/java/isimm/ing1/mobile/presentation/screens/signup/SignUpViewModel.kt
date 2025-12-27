package isimm.ing1.mobile.presentation.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class SignUpUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val gender: String = "Homme",
    val dateOfBirth: String = "",
    val termsAccepted: Boolean = false,
    
    // Errors
    val firstNameError: String = "",
    val lastNameError: String = "",
    val emailError: String = "",
    val phoneError: String = "",
    val passwordError: String = "",
    val termsError: String = "",
    
    // State
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SignUpViewModel : ViewModel() {
    var uiState by mutableStateOf(SignUpUiState())
        private set
    
    fun updateFirstName(value: String) = run { uiState = uiState.copy(firstName = value, firstNameError = "") }
    fun updateLastName(value: String) = run { uiState = uiState.copy(lastName = value, lastNameError = "") }
    fun updateEmail(value: String) = run { uiState = uiState.copy(email = value, emailError = "") }
    fun updatePhone(value: String) = run { uiState = uiState.copy(phone = value, phoneError = "") }
    fun updatePassword(value: String) = run { uiState = uiState.copy(password = value, passwordError = "") }
    fun updateGender(value: String) = run { uiState = uiState.copy(gender = value) }
    fun updateDateOfBirth(value: String) = run { uiState = uiState.copy(dateOfBirth = value) }
    fun updateTermsAccepted(value: Boolean) = run { uiState = uiState.copy(termsAccepted = value, termsError = "") }
    
    fun getPasswordStrength(): Int {
        val password = uiState.password
        return when {
            password.length < 6 -> 1
            password.length < 8 -> 2
            password.any { it.isUpperCase() } && password.any { it.isDigit() } -> 4
            else -> 3
        }
    }
    
    fun validateAndSignUp(onSuccess: (String) -> Unit) {
        var isValid = true
        var state = uiState
        
        if (state.firstName.isBlank()) {
            state = state.copy(firstNameError = "Prénom requis")
            isValid = false
        }
        if (state.lastName.isBlank()) {
            state = state.copy(lastNameError = "Nom requis")
            isValid = false
        }
        if (state.email.isBlank()) {
            state = state.copy(emailError = "Email requis")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(emailError = "Email invalide")
            isValid = false
        }
        if (state.phone.isBlank()) {
            state = state.copy(phoneError = "Numéro de téléphone requis")
            isValid = false
        }
        if (state.password.length < 6) {
            state = state.copy(passwordError = "Le mot de passe doit contenir au moins 6 caractères")
            isValid = false
        }
        if (!state.termsAccepted) {
            state = state.copy(termsError = "Vous devez accepter les conditions")
            isValid = false
        }
        
        uiState = state
        
        if (isValid) {
            // TODO: Implement actual signup with Firebase Auth
            uiState = uiState.copy(isLoading = true)
            onSuccess(uiState.email)
        }
    }
}
