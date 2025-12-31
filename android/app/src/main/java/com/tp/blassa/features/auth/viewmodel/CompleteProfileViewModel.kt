package com.tp.blassa.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.UserUpdateRequest
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompleteProfileUiState(
        val firstName: String = "",
        val lastName: String = "",
        val phoneNumber: String = "+216",
        val dateOfBirth: String = "",
        val gender: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val fieldErrors: Map<String, String> = emptyMap()
)

class CompleteProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = RetrofitClient.dashboardApiService.getUserProfile()
                _uiState.update {
                    it.copy(
                            firstName = profile.firstName,
                            lastName = profile.lastName,
                            phoneNumber =
                                    if (profile.phoneNumber.isNullOrBlank()) "+216"
                                    else profile.phoneNumber,
                            dateOfBirth = profile.dateOfBirth ?: "",
                            gender = profile.gender ?: "",
                            isLoading = false
                    )
                }
            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                            isLoading = false,
                            error = "Erreur de chargement du profil: ${e.message}"
                    )
                }
            }
        }
    }

    fun updatePhoneNumber(number: String) {
        _uiState.update {
            it.copy(phoneNumber = number, fieldErrors = it.fieldErrors - "phoneNumber")
        }
    }

    fun updateDateOfBirth(date: String) {
        _uiState.update {
            it.copy(dateOfBirth = date, fieldErrors = it.fieldErrors - "dateOfBirth")
        }
    }

    fun updateGender(gender: String) {
        _uiState.update { it.copy(gender = gender, fieldErrors = it.fieldErrors - "gender") }
    }

    private fun validate(): Boolean {
        val errors = mutableMapOf<String, String>()
        val state = _uiState.value

        if (!state.phoneNumber.matches(Regex("^\\+216[0-9]{8}$"))) {
            errors["phoneNumber"] = "Numéro invalide (+216XXXXXXXX)"
        }

        if (state.dateOfBirth.isBlank()) {
            errors["dateOfBirth"] = "Date requise"
        } else {
            try {
                val dob = LocalDate.parse(state.dateOfBirth)
                val eighteenYearsAgo = LocalDate.now().minusYears(18)
                if (dob.isAfter(eighteenYearsAgo)) {
                    errors["dateOfBirth"] = "Vous devez avoir au moins 18 ans"
                }
            } catch (e: Exception) {
                errors["dateOfBirth"] = "Format invalide"
            }
        }

        if (state.gender.isBlank()) {
            errors["gender"] = "Genre requis"
        }

        _uiState.update { it.copy(fieldErrors = errors) }
        return errors.isEmpty()
    }

    fun submitProfile() {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                RetrofitClient.dashboardApiService.updateUserProfile(
                        UserUpdateRequest(
                                firstName = _uiState.value.firstName,
                                lastName = _uiState.value.lastName,
                                phoneNumber = _uiState.value.phoneNumber,
                                dateOfBirth = _uiState.value.dateOfBirth,
                                gender = _uiState.value.gender
                        )
                )
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage =
                        try {
                            com.google.gson.JsonParser.parseString(errorBody)
                                    .asJsonObject
                                    .get("message")
                                    .asString
                        } catch (jsonException: Exception) {
                            "Une erreur est survenue"
                        }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Une erreur est survenue")
                }
            }
        }
    }
}
