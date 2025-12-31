package com.tp.blassa.features.history.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.WebSocketManager
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryRide(
        val id: String,
        val type: String,
        val origin: String,
        val destination: String,
        val departureTime: String,
        val price: Double,
        val status: String,
        val driverName: String? = null
)

data class RideHistoryUiState(
        val rides: List<HistoryRide> = emptyList(),
        val selectedFilter: String = "ALL",
        val isLoading: Boolean = true,
        val error: String? = null
)

class RideHistoryViewModel : ViewModel() {

    companion object {
        private const val TAG = "RideHistoryViewModel"
    }

    private val apiService = RetrofitClient.dashboardApiService

    private val _uiState = MutableStateFlow(RideHistoryUiState())
    val uiState: StateFlow<RideHistoryUiState> = _uiState.asStateFlow()

    init {
        loadRideHistory()
        observeRealTimeUpdates()
    }

    private fun observeRealTimeUpdates() {
        viewModelScope.launch {
            WebSocketManager.dashboardRefreshFlow.collect { event ->
                android.util.Log.d(TAG, "Real-time event received: $event, refreshing history...")
                loadRideHistory(isSilent = true)
            }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun refresh() {
        loadRideHistory()
    }

    fun getFilteredRides(): List<HistoryRide> {
        val state = _uiState.value
        return when (state.selectedFilter) {
            "DRIVER" -> state.rides.filter { it.type == "driver" }
            "PASSENGER" -> state.rides.filter { it.type == "passenger" }
            else -> state.rides
        }
    }

    private fun loadRideHistory(isSilent: Boolean = false) {
        if (!isSilent) {
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            try {
                val allRides = mutableListOf<HistoryRide>()
                var hasError = false
                var errorMessage = ""

                try {
                    Log.d(TAG, "Fetching driver rides...")
                    val ridesResponse = apiService.getMyRides(size = 100)
                    Log.d(TAG, "Driver rides fetched: ${ridesResponse.content.size} rides")

                    ridesResponse.content.forEach { ride ->
                        // Driver side: Only show COMPLETED or CANCELLED rides
                        if (ride.status == "COMPLETED" || ride.status == "CANCELLED") {
                            Log.d(TAG, "Adding driver ride: ${ride.id} - ${ride.status}")
                            allRides.add(
                                    HistoryRide(
                                            id = ride.id,
                                            type = "driver",
                                            origin = ride.originName,
                                            destination = ride.destinationName,
                                            departureTime = ride.departureTime,
                                            price = ride.pricePerSeat,
                                            status = ride.status
                                    )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch driver rides: ${e.message}", e)
                    hasError = true
                    errorMessage = "Erreur chargement trajets conducteur: ${e.message}"
                }

                try {
                    Log.d(TAG, "Fetching passenger bookings...")
                    val bookingsResponse = apiService.getMyBookings(size = 100)
                    Log.d(
                            TAG,
                            "Passenger bookings fetched: ${bookingsResponse.content.size} bookings"
                    )

                    bookingsResponse.content.forEach { booking ->
                        // Passenger side: Show if booking is REJECTED/CANCELLED OR if ride is
                        // COMPLETED/CANCELLED
                        val isHistory =
                                booking.status == "REJECTED" ||
                                        booking.status == "CANCELLED" ||
                                        (booking.status == "CONFIRMED" &&
                                                (booking.rideStatus == "COMPLETED" ||
                                                        booking.rideStatus == "CANCELLED"))

                        if (isHistory) {
                            Log.d(
                                    TAG,
                                    "Adding passenger booking: ${booking.id} - B:${booking.status}/R:${booking.rideStatus}"
                            )
                            val parts = booking.rideSummary.split("->")
                            allRides.add(
                                    HistoryRide(
                                            id = booking.rideID,
                                            type = "passenger",
                                            origin = parts.getOrElse(0) { "" }.trim(),
                                            destination = parts.getOrElse(1) { "" }.trim(),
                                            departureTime = booking.departureTime,
                                            price = booking.priceTotal,
                                            status =
                                                    if (booking.status == "CONFIRMED")
                                                            booking.rideStatus
                                                    else booking.status,
                                            driverName = booking.driverName
                                    )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch passenger bookings: ${e.message}", e)
                    hasError = true
                    errorMessage +=
                            if (errorMessage.isEmpty())
                                    "Erreur chargement réservations: ${e.message}"
                            else " | Erreur chargement réservations: ${e.message}"
                }

                Log.d(TAG, "Total rides loaded: ${allRides.size}")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val sortedRides =
                        allRides.distinctBy { "${it.id}_${it.type}" }.sortedByDescending {
                            try {
                                dateFormat.parse(it.departureTime)?.time ?: 0
                            } catch (e: Exception) {
                                0
                            }
                        }

                _uiState.update {
                    it.copy(
                            isLoading = false,
                            rides = sortedRides,
                            error = if (hasError && sortedRides.isEmpty()) errorMessage else null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load ride history: ${e.message}", e)
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            error = "Impossible de charger l'historique: ${e.message}"
                    )
                }
            }
        }
    }
}
