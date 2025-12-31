package com.tp.blassa.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.WebSocketManager
import com.tp.blassa.core.notification.NotificationCountManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardStats(
        val totalTrips: Int = 0,
        val totalRides: Int = 0,
        val earnings: Double = 0.0,
        val rating: Double = 0.0
)

data class UpcomingRide(
        val id: String,
        val type: String,
        val origin: String,
        val destination: String,
        val departureTime: String,
        val price: Double,
        val status: String
)

data class DashboardUiState(
        val stats: DashboardStats = DashboardStats(),
        val upcomingRides: List<UpcomingRide> = emptyList(),
        val selectedFilter: String = "ALL",
        val isLoading: Boolean = true,
        val error: String? = null,
        val selectedTab: Int = 0,
        val unreadNotificationsCount: Int = 0
)

class DashboardViewModel : ViewModel() {

    private val apiService = RetrofitClient.dashboardApiService
    private val webSocketManager = WebSocketManager

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUpcomingRides()
        loadUnreadNotificationsCount()

        webSocketManager.connect()
        observeRealTimeNotifications()
        observeSharedNotificationCount()
    }

    private fun observeSharedNotificationCount() {
        viewModelScope.launch {
            NotificationCountManager.unreadCount.collect { count ->
                _uiState.update { it.copy(unreadNotificationsCount = count) }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun refresh() {
        loadUpcomingRides()
        loadUnreadNotificationsCount()
    }

    fun getFilteredRides(): List<UpcomingRide> {
        val state = _uiState.value
        return when (state.selectedFilter) {
            "DRIVER" -> state.upcomingRides.filter { it.type == "driver" }
            "PASSENGER" -> state.upcomingRides.filter { it.type == "passenger" }
            else -> state.upcomingRides
        }
    }

    private fun loadUpcomingRides() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val ridesResult = runCatching { apiService.getMyRides() }
                val bookingsResult = runCatching { apiService.getMyBookings() }
                val reviewsResult = runCatching { apiService.getMyReceivedReviews() }

                var totalTrips = 0
                var totalRides = 0
                var earnings = 0.0
                var avgRating = 0.0
                val rides = mutableListOf<UpcomingRide>()

                ridesResult.getOrNull()?.let { response ->
                    totalRides = response.page.totalElements
                    response.content
                            .filter { it.status in listOf("SCHEDULED", "FULL", "IN_PROGRESS") }
                            .forEach { ride ->
                                rides.add(
                                        UpcomingRide(
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
                    response.content.filter { it.status == "COMPLETED" }.forEach { ride ->
                        totalTrips++
                        earnings += (ride.totalSeats - ride.availableSeats) * ride.pricePerSeat
                    }
                }

                bookingsResult.getOrNull()?.let { response ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                    val now = Date()

                    // Active ride statuses that should show on dashboard
                    val activeRideStatuses = listOf("SCHEDULED", "FULL", "IN_PROGRESS")

                    response.content
                            .filter { booking ->
                                // Show if booking is confirmed/pending AND ride is active (not
                                // completed/cancelled)
                                booking.status in listOf("CONFIRMED", "PENDING") &&
                                        booking.rideStatus in activeRideStatuses
                            }
                            .filter {
                                try {
                                    dateFormat.parse(it.departureTime)?.after(now) ?: false
                                } catch (e: Exception) {
                                    true
                                }
                            }
                            .forEach { booking ->
                                android.util.Log.d(
                                        "DashboardVM",
                                        "Adding booking: ${booking.rideSummary}, status=${booking.status}, rideStatus=${booking.rideStatus}"
                                )
                                val parts = booking.rideSummary.split("->")
                                rides.add(
                                        UpcomingRide(
                                                id = booking.rideID,
                                                type = "passenger",
                                                origin = parts.getOrElse(0) { "" }.trim(),
                                                destination = parts.getOrElse(1) { "" }.trim(),
                                                departureTime = booking.departureTime,
                                                price = booking.priceTotal,
                                                status = booking.status
                                        )
                                )
                            }
                    totalTrips +=
                            response.content.count {
                                it.status == "CONFIRMED" && it.rideStatus == "COMPLETED"
                            }
                }

                reviewsResult.getOrNull()?.let { response ->
                    if (response.content.isNotEmpty()) {
                        avgRating =
                                response.content.map { it.rating }.average().let {
                                    Math.round(it * 10) / 10.0
                                }
                    }
                }

                val sortedRides =
                        rides
                                .sortedBy {
                                    try {
                                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                                                .parse(it.departureTime)
                                                ?.time
                                                ?: 0
                                    } catch (e: Exception) {
                                        0
                                    }
                                }
                                .distinctBy { it.id }

                _uiState.update {
                    it.copy(
                            isLoading = false,
                            stats = DashboardStats(totalTrips, totalRides, earnings, avgRating),
                            upcomingRides = sortedRides
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Impossible de charger les données")
                }
            }
        }
    }
    private fun loadUnreadNotificationsCount() {
        viewModelScope.launch {
            try {
                val notifications = RetrofitClient.dashboardApiService.getNotifications()
                val unreadCount = notifications.count { !it.isRead }
                // Update both local state and shared manager
                NotificationCountManager.setCount(unreadCount)
                _uiState.update { it.copy(unreadNotificationsCount = unreadCount) }
            } catch (e: Exception) {
                // Ignore error for badge count
            }
        }
    }

    private fun observeRealTimeNotifications() {
        // Observe notification flow for badge count
        viewModelScope.launch {
            webSocketManager.notificationFlow.collect { _ ->
                // Use shared manager for notification count
                NotificationCountManager.increment()
            }
        }

        // Observe dashboard refresh events for auto-updating data
        viewModelScope.launch {
            webSocketManager.dashboardRefreshFlow.collect { event ->
                android.util.Log.d(
                        "DashboardViewModel",
                        "Real-time event received: $event, auto-refreshing..."
                )
                // Auto-refresh dashboard data when relevant events occur
                when (event) {
                    WebSocketManager.RealTimeEvent.BOOKING_RECEIVED,
                    WebSocketManager.RealTimeEvent.BOOKING_ACCEPTED,
                    WebSocketManager.RealTimeEvent.BOOKING_REJECTED,
                    WebSocketManager.RealTimeEvent.BOOKING_CANCELLED,
                    WebSocketManager.RealTimeEvent.RIDE_UPDATED,
                    WebSocketManager.RealTimeEvent.RIDE_CANCELLED -> {
                        // Reload rides data silently (without showing loading indicator)
                        loadUpcomingRidesSilently()
                    }
                    WebSocketManager.RealTimeEvent.NEW_REVIEW -> {
                        // Just reload stats for new reviews
                        loadUpcomingRidesSilently()
                    }
                    WebSocketManager.RealTimeEvent.GENERAL_UPDATE -> {
                        // For general updates, just increment notification count
                        // Data is already updated via notificationFlow
                    }
                }
            }
        }
    }

    private fun loadUpcomingRidesSilently() {
        // Similar to loadUpcomingRides but without showing loading indicator
        viewModelScope.launch {
            try {
                val ridesResult = runCatching { apiService.getMyRides() }
                val bookingsResult = runCatching { apiService.getMyBookings() }
                val reviewsResult = runCatching { apiService.getMyReceivedReviews() }

                var totalTrips = 0
                var totalRides = 0
                var earnings = 0.0
                var avgRating = 0.0
                val rides = mutableListOf<UpcomingRide>()

                ridesResult.getOrNull()?.let { response ->
                    totalRides = response.page.totalElements
                    response.content
                            .filter { it.status in listOf("SCHEDULED", "FULL", "IN_PROGRESS") }
                            .forEach { ride ->
                                rides.add(
                                        UpcomingRide(
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
                    response.content.filter { it.status == "COMPLETED" }.forEach { ride ->
                        totalTrips++
                        earnings += (ride.totalSeats - ride.availableSeats) * ride.pricePerSeat
                    }
                }

                bookingsResult.getOrNull()?.let { response ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                    val now = Date()

                    // Active ride statuses that should show on dashboard
                    val activeRideStatuses = listOf("SCHEDULED", "FULL", "IN_PROGRESS")

                    response.content
                            .filter { booking ->
                                booking.status in listOf("CONFIRMED", "PENDING") &&
                                        booking.rideStatus in activeRideStatuses
                            }
                            .filter {
                                try {
                                    dateFormat.parse(it.departureTime)?.after(now) ?: false
                                } catch (e: Exception) {
                                    true
                                }
                            }
                            .forEach { booking ->
                                val parts = booking.rideSummary.split("->")
                                rides.add(
                                        UpcomingRide(
                                                id = booking.rideID,
                                                type = "passenger",
                                                origin = parts.getOrElse(0) { "" }.trim(),
                                                destination = parts.getOrElse(1) { "" }.trim(),
                                                departureTime = booking.departureTime,
                                                price = booking.priceTotal,
                                                status = booking.status
                                        )
                                )
                            }
                    totalTrips +=
                            response.content.count {
                                it.status == "CONFIRMED" && it.rideStatus == "COMPLETED"
                            }
                }

                reviewsResult.getOrNull()?.let { response ->
                    if (response.content.isNotEmpty()) {
                        avgRating =
                                response.content.map { it.rating }.average().let {
                                    Math.round(it * 10) / 10.0
                                }
                    }
                }

                val sortedRides =
                        rides
                                .sortedBy {
                                    try {
                                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                                                .parse(it.departureTime)
                                                ?.time
                                                ?: 0
                                    } catch (e: Exception) {
                                        0
                                    }
                                }
                                .distinctBy { it.id }

                _uiState.update {
                    it.copy(
                            stats = DashboardStats(totalTrips, totalRides, earnings, avgRating),
                            upcomingRides = sortedRides
                    )
                }
                android.util.Log.d(
                        "DashboardViewModel",
                        "Dashboard silently refreshed with ${sortedRides.size} rides"
                )
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Silent refresh failed", e)
                // Silently fail - don't show error to user
            }
        }
    }
}
