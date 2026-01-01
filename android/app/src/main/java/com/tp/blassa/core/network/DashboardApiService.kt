package com.tp.blassa.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class RideStatusResponse(val rideId: String, val status: String)

data class PagedResponse<T>(val content: List<T>, val page: PageMetadata)

data class PageMetadata(
        val size: Int,
        val totalElements: Int,
        val totalPages: Int,
        val number: Int
)

data class Ride(
        val id: String,
        val originName: String,
        val destinationName: String,
        val departureTime: String,
        val totalSeats: Int,
        val availableSeats: Int,
        val pricePerSeat: Double,
        val status: String,
        val driverName: String? = null,
        val driverProfilePictureUrl: String? = null,
        val driverRating: Double? = null,
        val genderPreference: String = "ANY",
        val allowsSmoking: Boolean = false,
        val allowsMusic: Boolean = false,
        val allowsPets: Boolean = false,
        val luggageSize: String = "MEDIUM"
)

data class Booking(
        val id: String,
        val rideID: String,
        val rideSummary: String,
        val departureTime: String,
        val seatsBooked: Int,
        val priceTotal: Double,
        val status: String,
        val rideStatus: String,
        val driverName: String? = null,
        val carLicensePlate: String? = null,
        val carDescription: String? = null
)

data class Review(
        val id: String,
        val rating: Int,
        val comment: String? = null,
        val reviewerName: String,
        val createdAt: String
)

data class Vehicle(
        val id: String,
        val make: String,
        val model: String,
        val color: String,
        val licensePlate: String,
        val productionYear: Int?
)

data class RideRequest(
        val originName: String,
        val originLat: Double,
        val originLon: Double,
        val destinationName: String,
        val destinationLat: Double,
        val destinationLon: Double,
        val departureTime: String,
        val totalSeats: Int,
        val pricePerSeat: Double,
        val allowsSmoking: Boolean = false,
        val allowsMusic: Boolean = false,
        val allowsPets: Boolean = false,
        val luggageSize: String = "MEDIUM",
        val genderPreference: String = "ANY",
        val vehicleId: String
)

data class RideResponse(
        val id: String,
        val originName: String,
        val destinationName: String,
        val departureTime: String,
        val totalSeats: Int,
        val availableSeats: Int,
        val pricePerSeat: Double,
        val status: String
)

data class VehicleRequest(
        val make: String,
        val model: String,
        val color: String,
        val licensePlate: String,
        val productionYear: Int?
)

data class RideDetail(
        val id: String,
        val driverId: String,
        val driverName: String,
        val driverEmail: String,
        val driverProfilePictureUrl: String?,
        val driverRating: Double?,
        val driverPhoneNumber: String?,
        val driverFacebookUrl: String?,
        val driverInstagramUrl: String?,
        val originName: String,
        val originLat: Double,
        val originLon: Double,
        val destinationName: String,
        val destinationLat: Double,
        val destinationLon: Double,
        val departureTime: String,
        val totalSeats: Int,
        val availableSeats: Int,
        val pricePerSeat: Double,
        val allowsSmoking: Boolean,
        val allowsMusic: Boolean? = false,
        val allowsPets: Boolean? = false,
        val luggageSize: String? = "MEDIUM",
        val genderPreference: String,
        val status: String,
        val carMake: String?,
        val carModel: String?,
        val carColor: String?,
        val carLicensePlate: String?
)

data class PassengerInfo(
        val bookingId: String,
        val passengerId: String,
        val passengerName: String,
        val passengerEmail: String,
        val passengerPhone: String,
        val passengerProfilePictureUrl: String?,
        val facebookUrl: String?,
        val instagramUrl: String?,
        val seatsBooked: Int,
        val status: String
)

data class Notification(
        val id: String,
        val userId: String? = null,
        val type: String,
        val title: String,
        val message: String,
        val isRead: Boolean,
        val link: String?,
        val createdAt: String
)

data class BookRideRequest(val rideId: String, val seatsRequested: Int)

data class PublicProfileResponse(
        val id: String,
        val firstName: String,
        val lastName: String,
        val bio: String?,
        val profilePictureUrl: String?,
        val gender: String?,
        val memberSince: String?,
        val completedRidesCount: Int?,
        val averageRating: Double?
)

data class UserReview(
        val id: String,
        val reviewerName: String,
        val reviewerProfilePictureUrl: String?,
        val rating: Int,
        val comment: String?,
        val createdAt: String
)

interface DashboardApiService {
        @GET("rides/mine")
        suspend fun getMyRides(
                @Query("page") page: Int = 0,
                @Query("size") size: Int = 50
        ): PagedResponse<Ride>

        @GET("rides/{id}") suspend fun getRideById(@Path("id") id: String): RideDetail

        @GET("bookings/ride/{id}/passengers")
        suspend fun getRidePassengers(@Path("id") rideId: String): List<PassengerInfo>

        @GET("rides/search")
        @Headers("No-Auth: true")
        suspend fun searchRides(
                @Query("originLat") originLat: Double,
                @Query("originLon") originLon: Double,
                @Query("destLat") destLat: Double,
                @Query("destLon") destLon: Double,
                @Query("departureTime") departureTime: String?,
                @Query("seats") seats: Int,
                @Query("genderFilter") genderFilter: String? = null,
                @Query("page") page: Int = 0,
                @Query("size") size: Int = 10
        ): PagedResponse<Ride>

        @GET("bookings/mine")
        suspend fun getMyBookings(
                @Query("page") page: Int = 0,
                @Query("size") size: Int = 50
        ): PagedResponse<Booking>

        @GET("reviews/mine/received")
        suspend fun getMyReceivedReviews(
                @Query("page") page: Int = 0,
                @Query("size") size: Int = 100
        ): PagedResponse<Review>

        @GET("vehicles") suspend fun getMyVehicles(): List<Vehicle>

        @POST("vehicles") suspend fun createVehicle(@Body request: VehicleRequest): Vehicle

        @DELETE("vehicles/{id}") suspend fun deleteVehicle(@Path("id") id: String)

        @POST("rides") suspend fun createRide(@Body request: RideRequest): RideResponse

        @POST("bookings") suspend fun bookRide(@Body request: BookRideRequest): Booking

        @GET("bookings/ride/{rideId}/mine")
        suspend fun getMyBookingForRide(@Path("rideId") rideId: String): Booking?

        @POST("bookings/{id}/accept")
        suspend fun acceptBooking(@Path("id") bookingId: String): Booking

        @POST("bookings/{id}/reject") suspend fun rejectBooking(@Path("id") bookingId: String)

        @DELETE("bookings/{id}") suspend fun cancelBooking(@Path("id") bookingId: String)

        @PUT("rides/{id}/start")
        suspend fun startRide(@Path("id") rideId: String): RideStatusResponse

        @PUT("rides/{id}/complete")
        suspend fun completeRide(@Path("id") rideId: String): RideStatusResponse

        @DELETE("rides/{id}") suspend fun cancelRide(@Path("id") rideId: String)

        @GET("user/me") suspend fun getUserProfile(): UserProfile

        @PUT("user/me") suspend fun updateUserProfile(@Body request: UserUpdateRequest): UserProfile

        @GET("notifications") suspend fun getNotifications(): List<Notification>

        @POST("notifications/{id}/read") suspend fun markNotificationRead(@Path("id") id: String)

        @POST("notifications/read-all") suspend fun markAllNotificationsRead()

        @GET("user/{userId}/public")
        suspend fun getPublicProfile(@Path("userId") userId: String): PublicProfileResponse

        @GET("reviews/user/{userId}")
        suspend fun getUserReviews(
                @Path("userId") userId: String,
                @Query("page") page: Int = 0,
                @Query("size") size: Int = 20
        ): PagedResponse<Review>

        @POST("reviews") suspend fun submitReview(@Body request: ReviewRequest): Review

        @GET("reviews/mine/sent")
        suspend fun getMySentReviews(
                @Query("page") page: Int = 0,
                @Query("size") size: Int = 100
        ): PagedResponse<SentReview>

        @Multipart
        @POST("user/me/picture")
        suspend fun uploadProfilePicture(@Part file: okhttp3.MultipartBody.Part): UserProfile

        @DELETE("user/me/picture") suspend fun deleteProfilePicture(): UserProfile

        @PUT("user/me/password")
        suspend fun changePassword(@Body request: ChangePasswordRequest): retrofit2.Response<Unit>

        @PUT("user/me/email")
        suspend fun changeEmail(@Body request: ChangeEmailRequest): ChangeEmailResponse

        @POST("reports")
        suspend fun createReport(@Body request: ReportRequest): retrofit2.Response<Unit>
}

data class ReportRequest(
        val reportedUserId: String? = null,
        val rideId: String? = null,
        val reason: String,
        val description: String
)

data class ReviewRequest(val bookingId: String, val rating: Int, val comment: String? = null)

data class SentReview(
        val id: String,
        val bookingId: String,
        val rating: Int,
        val comment: String?,
        val createdAt: String
)

data class UserProfile(
        val id: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val phoneNumber: String?,
        @SerializedName("dob") val dateOfBirth: String?,
        val gender: String?,
        val bio: String?,
        val profilePictureUrl: String?,
        val isVerified: Boolean,
        val facebookUrl: String? = null,
        val instagramUrl: String? = null
)

data class UserUpdateRequest(
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        @SerializedName("dob") val dateOfBirth: String,
        val gender: String,
        val bio: String? = null,
        val facebookUrl: String? = null,
        val instagramUrl: String? = null
)

data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

data class ChangeEmailRequest(val newEmail: String, val password: String)

data class ChangeEmailResponse(val profile: UserProfile, val accessToken: String)
