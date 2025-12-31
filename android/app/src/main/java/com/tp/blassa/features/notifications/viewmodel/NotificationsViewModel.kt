package com.tp.blassa.features.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.Notification
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.notification.NotificationCountManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsUiState(
        val notifications: List<Notification> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
)

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        observeRealTimeNotifications()
    }

    private fun observeRealTimeNotifications() {
        viewModelScope.launch {
            com.tp.blassa.core.network.WebSocketManager.notificationFlow.collect { notification ->
                android.util.Log.d(
                        "NotificationsVM",
                        "Real-time notification received: ${notification.id}"
                )
                _uiState.update { state ->
                    // Prepend new notification to the top of the list
                    // Avoid duplicates just in case
                    val currentList = state.notifications
                    if (currentList.none { it.id == notification.id }) {
                        state.copy(notifications = listOf(notification) + currentList)
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val notifications = RetrofitClient.dashboardApiService.getNotifications()
                val duplicates = notifications.groupBy { it.id }.filter { it.value.size > 1 }
                if (duplicates.isNotEmpty()) {
                    android.util.Log.e(
                            "NotificationsVM",
                            "DUPLICATE NOTIFICATION IBS FOUND: ${duplicates.keys}"
                    )
                }
                _uiState.update { it.copy(notifications = notifications, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Impossible de charger les notifications")
                }
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                // Check if notification was unread before marking
                val notification = _uiState.value.notifications.find { it.id == notificationId }
                val wasUnread = notification?.isRead == false

                android.util.Log.d(
                        "NotificationsVM",
                        "markAsRead: id=$notificationId, wasUnread=$wasUnread, currentCount=${NotificationCountManager.unreadCount.value}"
                )

                RetrofitClient.dashboardApiService.markNotificationRead(notificationId)
                _uiState.update { state ->
                    val updatedList =
                            state.notifications.map {
                                if (it.id == notificationId) it.copy(isRead = true) else it
                            }
                    state.copy(notifications = updatedList)
                }

                // Decrement shared count if notification was unread
                if (wasUnread) {
                    android.util.Log.d(
                            "NotificationsVM",
                            "Decrementing count from ${NotificationCountManager.unreadCount.value}"
                    )
                    NotificationCountManager.decrement()
                    android.util.Log.d(
                            "NotificationsVM",
                            "Count after decrement: ${NotificationCountManager.unreadCount.value}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("NotificationsVM", "Error marking as read", e)
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.d("NotificationsVM", "Marking ALL as read")
                RetrofitClient.dashboardApiService.markAllNotificationsRead()

                // Reset shared notification count
                NotificationCountManager.reset()

                loadNotifications()
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                android.util.Log.e("NotificationsVM", "Failed to mark all read", e)
            }
        }
    }
}
