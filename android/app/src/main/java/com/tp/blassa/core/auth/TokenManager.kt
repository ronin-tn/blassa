package com.tp.blassa.core.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenManager {
    private const val PREFS_NAME = "blassa_secure_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            val masterKey =
                    MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

            prefs =
                    EncryptedSharedPreferences.create(
                            context,
                            PREFS_NAME,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String?) {
        prefs?.edit()?.apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            apply()
        }
    }

    fun saveUserEmail(email: String) {
        prefs?.edit()?.putString(KEY_USER_EMAIL, email)?.apply()
    }

    fun getUserEmail(): String? = prefs?.getString(KEY_USER_EMAIL, null)

    fun getAccessToken(): String? = prefs?.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs?.getString(KEY_REFRESH_TOKEN, null)

    fun clearTokens() {
        val onboardingCompleted = hasCompletedOnboarding()
        prefs?.edit()?.clear()?.apply()
        if (onboardingCompleted) {
            setOnboardingCompleted(true)
        }
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun hasCompletedOnboarding(): Boolean =
            prefs?.getBoolean(KEY_ONBOARDING_COMPLETED, false) ?: false

    fun setOnboardingCompleted(completed: Boolean) {
        prefs?.edit()?.putBoolean(KEY_ONBOARDING_COMPLETED, completed)?.apply()
    }
}
