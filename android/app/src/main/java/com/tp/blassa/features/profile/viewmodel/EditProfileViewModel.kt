package com.tp.blassa.features.profile.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.UserProfile
import com.tp.blassa.core.network.UserUpdateRequest
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

data class EditProfileUiState(
        val profile: UserProfile? = null,
        val firstName: String = "",
        val lastName: String = "",
        val phoneNumber: String = "",
        val bio: String = "",
        val facebookUrl: String = "",
        val instagramUrl: String = "",
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val isUploadingPicture: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
)

class EditProfileViewModel : ViewModel() {

    companion object {
        private const val TAG = "EditProfileViewModel"
    }

    private val apiService = RetrofitClient.dashboardApiService

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val profile = apiService.getUserProfile()
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            profile = profile,
                            firstName = profile.firstName,
                            lastName = profile.lastName,
                            phoneNumber = profile.phoneNumber ?: "",
                            bio = profile.bio ?: "",
                            facebookUrl = profile.facebookUrl ?: "",
                            instagramUrl = profile.instagramUrl ?: ""
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load profile: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Impossible de charger le profil")
                }
            }
        }
    }

    fun updateFirstName(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun updateLastName(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun updatePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun updateBio(value: String) {
        _uiState.update { it.copy(bio = value) }
    }

    fun updateFacebookUrl(value: String) {
        _uiState.update { it.copy(facebookUrl = value) }
    }

    fun updateInstagramUrl(value: String) {
        _uiState.update { it.copy(instagramUrl = value) }
    }

    fun saveProfile() {
        val state = _uiState.value
        val profile = state.profile ?: return

        _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }

        viewModelScope.launch {
            try {
                val request =
                        UserUpdateRequest(
                                firstName = state.firstName,
                                lastName = state.lastName,
                                phoneNumber = state.phoneNumber,
                                dateOfBirth = profile.dateOfBirth ?: "",
                                gender = profile.gender ?: "",
                                bio = state.bio.ifEmpty { null },
                                facebookUrl = state.facebookUrl.ifEmpty { null },
                                instagramUrl = state.instagramUrl.ifEmpty { null }
                        )

                val updatedProfile = apiService.updateUserProfile(request)
                _uiState.update {
                    it.copy(
                            isSaving = false,
                            profile = updatedProfile,
                            successMessage = "Profil mis à jour avec succès"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save profile: ${e.message}", e)
                _uiState.update {
                    it.copy(isSaving = false, error = "Erreur lors de la mise à jour")
                }
            }
        }
    }

    fun uploadProfilePicture(context: Context, uri: Uri) {
        _uiState.update { it.copy(isUploadingPicture = true, error = null) }

        viewModelScope.launch {
            try {
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

                val allowedTypes = listOf("image/jpeg", "image/png", "image/webp", "image/gif")
                if (mimeType !in allowedTypes) {
                    _uiState.update {
                        it.copy(
                                isUploadingPicture = false,
                                error =
                                        "Type de fichier non supporté. Utilisez JPEG, PNG, WebP ou GIF"
                        )
                    }
                    return@launch
                }

                val extension =
                        when (mimeType) {
                            "image/jpeg" -> "jpg"
                            "image/png" -> "png"
                            "image/webp" -> "webp"
                            "image/gif" -> "gif"
                            else -> "jpg"
                        }

                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile =
                        File(
                                context.cacheDir,
                                "profile_picture_${System.currentTimeMillis()}.$extension"
                        )
                tempFile.outputStream().use { outputStream -> inputStream?.copyTo(outputStream) }
                inputStream?.close()

                val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val multipartBody =
                        MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

                val updatedProfile = apiService.uploadProfilePicture(multipartBody)

                tempFile.delete()

                _uiState.update {
                    it.copy(
                            isUploadingPicture = false,
                            profile = updatedProfile,
                            successMessage = "Photo de profil mise à jour"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload profile picture: ${e.message}", e)
                _uiState.update {
                    it.copy(
                            isUploadingPicture = false,
                            error = "Erreur lors de l'upload de la photo"
                    )
                }
            }
        }
    }

    fun deleteProfilePicture() {
        _uiState.update { it.copy(isUploadingPicture = true, error = null) }

        viewModelScope.launch {
            try {
                val updatedProfile = apiService.deleteProfilePicture()
                _uiState.update {
                    it.copy(
                            isUploadingPicture = false,
                            profile = updatedProfile,
                            successMessage = "Photo de profil supprimée"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete profile picture: ${e.message}", e)
                _uiState.update {
                    it.copy(isUploadingPicture = false, error = "Erreur lors de la suppression")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
