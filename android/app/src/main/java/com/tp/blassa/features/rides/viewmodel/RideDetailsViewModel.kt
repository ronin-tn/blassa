package com.tp.blassa.features.rides.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.network.PassengerInfo
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.core.network.WebSocketManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RideDetailsUiState(
        val isLoading: Boolean = true,
        val isActionLoading: Boolean = false,
        val error: String? = null,
        val ride: RideDetail? = null,
        val passengers: List<PassengerInfo> = emptyList(),
        val currentUserEmail: String? = null,
        val isOwnRide: Boolean = false,
        val myBooking: com.tp.blassa.core.network.Booking? = null,
        val isReviewSubmitting: String? = null,
        val reviewedPassengers: Set<String> = emptySet(),
        val hasExistingReview: Boolean = false,
        val isReviewSubmitted: Boolean = false
)

class RideDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RideDetailsUiState())
    val uiState: StateFlow<RideDetailsUiState> = _uiState.asStateFlow()

    private var updateJob: Job? = null
    private var currentRideId: String? = null

    fun initialize(context: Context) {
        val email = TokenManager.getUserEmail()
        _uiState.update { it.copy(currentUserEmail = email) }
    }

    fun loadRideDetails(rideId: String, isSilent: Boolean = false) {
        currentRideId = rideId
        startRealTimeUpdates(rideId)

        viewModelScope.launch {
            if (!isSilent) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            try {

                val ride = RetrofitClient.dashboardApiService.getRideById(rideId)

                val currentEmail = _uiState.value.currentUserEmail
                val isOwn =
                        currentEmail != null &&
                                ride.driverEmail.equals(currentEmail, ignoreCase = true)

                val passengers =
                        if (isOwn) {
                            try {
                                RetrofitClient.dashboardApiService.getRidePassengers(rideId)
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } else {
                            emptyList()
                        }

                val myBooking =
                        if (!isOwn && currentEmail != null) {
                            try {
                                RetrofitClient.dashboardApiService.getMyBookingForRide(rideId)
                            } catch (e: Exception) {
                                null // No booking or 404
                            }
                        } else {
                            null
                        }

                _uiState.update {
                    it.copy(
                            isLoading = false,
                            ride = ride,
                            isOwnRide = isOwn,
                            passengers = passengers,
                            myBooking = myBooking
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            error = "Impossible de charger les détails du trajet"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    private fun startRealTimeUpdates(rideId: String) {
        updateJob?.cancel()
        updateJob =
                viewModelScope.launch {
                    WebSocketManager.notificationFlow.collect { notification ->
                        // Check if notification is related to this ride
                        if (notification.link?.contains(rideId) == true) {
                            android.util.Log.d("RideDetailsVM", "Real-time update for ride $rideId")
                            // Reload silently
                            loadRideDetails(rideId, isSilent = true)
                        }
                    }
                }
    }

    fun bookRide(rideId: String, seats: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                val request =
                        com.tp.blassa.core.network.BookRideRequest(
                                rideId = rideId,
                                seatsRequested = seats
                        )
                RetrofitClient.dashboardApiService.bookRide(request)
                loadRideDetails(rideId) // Refresh to show pending status
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun cancelBooking(bookingId: String, rideId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                RetrofitClient.dashboardApiService.cancelBooking(bookingId)
                loadRideDetails(rideId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun acceptBooking(bookingId: String, rideId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                RetrofitClient.dashboardApiService.acceptBooking(bookingId)
                loadRideDetails(rideId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun rejectBooking(bookingId: String, rideId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                RetrofitClient.dashboardApiService.rejectBooking(bookingId)
                loadRideDetails(rideId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun startRide(rideId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                RetrofitClient.dashboardApiService.startRide(rideId)
                loadRideDetails(rideId) // Refresh, now status should be IN_PROGRESS
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun completeRide(rideId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                RetrofitClient.dashboardApiService.completeRide(rideId)
                loadRideDetails(rideId) // Refresh, now status should be COMPLETED
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun cancelRide(rideId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                RetrofitClient.dashboardApiService.cancelRide(rideId)
                loadRideDetails(rideId) // Refresh
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun retry(rideId: String) {
        loadRideDetails(rideId)
    }

    fun checkExistingReviews(bookingIds: List<String>) {
        viewModelScope.launch {
            try {
                val sentReviews = RetrofitClient.dashboardApiService.getMySentReviews()
                val reviewedIds =
                        sentReviews
                                .content
                                .filter { bookingIds.contains(it.bookingId) }
                                .map { it.bookingId }
                                .toSet()

                val myBookingId = _uiState.value.myBooking?.id
                val hasExisting = myBookingId != null && reviewedIds.contains(myBookingId)

                _uiState.update {
                    it.copy(reviewedPassengers = reviewedIds, hasExistingReview = hasExisting)
                }
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    fun submitReview(bookingId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isReviewSubmitting = bookingId) }
            try {
                val request =
                        com.tp.blassa.core.network.ReviewRequest(
                                bookingId = bookingId,
                                rating = rating,
                                comment = comment.ifBlank { null }
                        )
                RetrofitClient.dashboardApiService.submitReview(request)

                val currentReviewed = _uiState.value.reviewedPassengers
                _uiState.update {
                    it.copy(
                            reviewedPassengers = currentReviewed + bookingId,
                            isReviewSubmitted = true
                    )
                }
            } catch (e: Exception) {

                if (e.message?.contains("already") == true || e.message?.contains("déjà") == true) {
                    val currentReviewed = _uiState.value.reviewedPassengers
                    _uiState.update {
                        it.copy(
                                reviewedPassengers = currentReviewed + bookingId,
                                hasExistingReview = true
                        )
                    }
                }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isReviewSubmitting = null) }
            }
        }
    }

    fun submitReport(
            rideId: String? = null,
            reportedUserId: String? = null,
            reason: String,
            description: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request =
                        com.tp.blassa.core.network.ReportRequest(
                                reportedUserId = reportedUserId,
                                rideId = rideId,
                                reason = reason,
                                description = description
                        )
                val response = RetrofitClient.dashboardApiService.createReport(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Erreur lors de l'envoi du signalement")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Une erreur est survenue")
            }
        }
    }
}
