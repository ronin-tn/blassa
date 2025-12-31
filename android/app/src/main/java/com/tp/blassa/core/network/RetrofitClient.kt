package com.tp.blassa.core.network

import com.tp.blassa.BuildConfig
import com.tp.blassa.core.auth.TokenManager
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val loggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val authInterceptor = Interceptor { chain ->
        var request = chain.request()

        if (request.header("No-Auth") != null) {
            request = request.newBuilder().removeHeader("No-Auth").build()
        } else {
            val token = TokenManager.getAccessToken()
            if (token != null) {
                request = request.newBuilder().header("Authorization", "Bearer $token").build()
            }
        }
        chain.proceed(request)
    }

    private val okHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

    private val retrofit: Retrofit =
            Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val dashboardApiService: DashboardApiService = retrofit.create(DashboardApiService::class.java)
}
