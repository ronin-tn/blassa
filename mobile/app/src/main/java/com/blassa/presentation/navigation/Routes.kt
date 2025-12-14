package com.blassa.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for the Blassa app.
 * Using type-safe navigation with Kotlin serialization.
 */
sealed interface Route {
    
    @Serializable
    data object Splash : Route
    
    @Serializable
    data object Onboarding : Route
    
    @Serializable
    data object Login : Route
    
    @Serializable
    data object Register : Route
    
    @Serializable
    data object EmailVerification : Route
    
    @Serializable
    data object EmailVerified : Route
    
    @Serializable
    data object ForgotPassword : Route
    
    @Serializable
    data object ResetPassword : Route
    
    @Serializable
    data object Home : Route
    
    @Serializable
    data object SearchRide : Route
    
    @Serializable
    data class SearchResults(
        val originLat: Double,
        val originLon: Double,
        val originName: String,
        val destLat: Double,
        val destLon: Double,
        val destName: String,
        val date: String? = null,
        val seats: Int = 1
    ) : Route
    
    @Serializable
    data class RideDetails(val rideId: String) : Route
    
    @Serializable
    data object PublishRide : Route
    
    @Serializable
    data object MyRides : Route
    
    @Serializable
    data object MyBookings : Route
    
    @Serializable
    data object Profile : Route
    
    @Serializable
    data object EditProfile : Route
    
    @Serializable
    data class DriverProfile(val userId: String) : Route
    
    @Serializable
    data object Notifications : Route
    
    @Serializable
    data object Settings : Route
    
    @Serializable
    data object ChangePassword : Route
    
    @Serializable
    data class LeaveReview(val rideId: String, val revieweeId: String) : Route
}
