package com.tp.blassa.core.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton to manage notification count across different ViewModels. This ensures the notification
 * badge updates in real-time across all screens.
 */
object NotificationCountManager {
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    fun setCount(count: Int) {
        _unreadCount.value = count
    }

    fun increment() {
        _unreadCount.value++
    }

    fun decrement() {
        if (_unreadCount.value > 0) {
            _unreadCount.value--
        }
    }

    fun reset() {
        _unreadCount.value = 0
    }
}
