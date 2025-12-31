package com.tp.blassa.core.network

import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        val gender: String,
        val birthDate: String
)

data class MobileAuthResponse(
        val status: String,
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val expiresIn: Long? = null,
        val email: String? = null,
        val message: String? = null,
        val verificationSentAt: String? = null
)

data class ErrorResponse(
        val error: String? = null,
        val message: String? = null,
        val status: Int? = null
)

fun HttpException.parseErrorMessage(): String {
    return try {
        val errorBody = response()?.errorBody()?.string()
        if (errorBody != null) {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.message ?: getDefaultErrorMessage(code())
        } else {
            getDefaultErrorMessage(code())
        }
    } catch (e: Exception) {
        getDefaultErrorMessage(code())
    }
}

private fun getDefaultErrorMessage(code: Int): String {
    return when (code) {
        400 -> "Requête invalide"
        401 -> "Email ou mot de passe incorrect"
        403 -> "Accès refusé"
        404 -> "Ressource introuvable"
        409 -> "Cette ressource existe déjà"
        429 -> "Trop de tentatives. Réessayez plus tard."
        500, 502, 503 -> "Erreur serveur. Réessayez plus tard."
        else -> "Erreur (code $code)"
    }
}

interface AuthApiService {
    @POST("auth/mobile/login") suspend fun login(@Body request: LoginRequest): MobileAuthResponse

    @POST("auth/mobile/register")
    suspend fun register(@Body request: RegisterRequest): MobileAuthResponse

    @POST("auth/mobile/google")
    suspend fun googleAuth(@Body request: Map<String, String>): MobileAuthResponse

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Body request: Map<String, String>): MobileAuthResponse

    @POST("auth/mobile/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): MobileAuthResponse
}
