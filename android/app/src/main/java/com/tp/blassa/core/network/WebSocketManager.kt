package com.tp.blassa.core.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.tp.blassa.BuildConfig
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.notification.NotificationHelper
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WebSocketManager {
    private val gson = Gson()
    private val client = OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build()

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _notificationFlow = MutableSharedFlow<Notification>()
    val notificationFlow: SharedFlow<Notification> = _notificationFlow.asSharedFlow()

    private val _dashboardRefreshFlow = MutableSharedFlow<RealTimeEvent>()
    val dashboardRefreshFlow: SharedFlow<RealTimeEvent> = _dashboardRefreshFlow.asSharedFlow()

    private var isConnected = false

    private var appContext: Context? = null

    enum class RealTimeEvent {
        BOOKING_RECEIVED,
        BOOKING_ACCEPTED,
        BOOKING_REJECTED,
        BOOKING_CANCELLED,
        RIDE_UPDATED,
        RIDE_CANCELLED,
        NEW_REVIEW,
        GENERAL_UPDATE
    }

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun connect() {
        if (isConnected) return

        val token = TokenManager.getAccessToken()
        if (token == null) {
            Log.e(TAG, "Cannot connect: No access token")
            return
        }

        val baseUrl = BuildConfig.BASE_URL

        val wsUrl =
                baseUrl.replace("https://", "wss://")
                        .replace("http://", "ws://")
                        .replace("/api/v1/", "/ws")
                        .replace("/api/v1", "/ws")

        Log.d(TAG, "Connecting to WebSocket: $wsUrl")

        val request = Request.Builder().url(wsUrl).build()

        webSocket =
                client.newWebSocket(
                        request,
                        object : WebSocketListener() {
                            override fun onOpen(webSocket: WebSocket, response: Response) {
                                Log.d(TAG, "WebSocket Opened")
                                isConnected = true
                                sendConnectFrame(token)
                            }

                            override fun onMessage(webSocket: WebSocket, text: String) {
                                Log.d(TAG, "Message received: $text")
                                handleMessage(text)
                            }

                            override fun onClosing(
                                    webSocket: WebSocket,
                                    code: Int,
                                    reason: String
                            ) {
                                Log.d(TAG, "WebSocket Closing: $code / $reason")
                                isConnected = false
                            }

                            override fun onFailure(
                                    webSocket: WebSocket,
                                    t: Throwable,
                                    response: Response?
                            ) {
                                Log.e(TAG, "WebSocket Error", t)
                                isConnected = false

                                scope.launch {
                                    delay(5000)
                                    connect()
                                }
                            }
                        }
                )
    }

    private fun sendConnectFrame(token: String) {

        val connectFrame =
                "CONNECT\n" +
                        "accept-version:1.1,1.0\n" +
                        "heart-beat:10000,10000\n" +
                        "Authorization:Bearer $token\n" +
                        "\n" +
                        "\u0000"

        webSocket?.send(connectFrame)
    }

    private fun subscribeToNotifications() {

        val subscribeFrame =
                "SUBSCRIBE\n" +
                        "id:sub-0\n" +
                        "destination:/user/queue/notification\n" +
                        "\n" +
                        "\u0000"

        webSocket?.send(subscribeFrame)
        Log.d(TAG, "Subscribed to /user/queue/notification")
    }

    private fun handleMessage(text: String) {
        if (text.startsWith("CONNECTED")) {
            Log.d(TAG, "STOMP Connected")
            subscribeToNotifications()
        } else if (text.startsWith("MESSAGE")) {
            try {

                val bodyStartIndex = text.indexOf("\n\n") + 2
                if (bodyStartIndex > 1) {
                    val body = text.substring(bodyStartIndex).trim().replace("\u0000", "")
                    Log.d(TAG, "Parsed Body: $body")

                    if (body.isNotEmpty()) {
                        val notification = gson.fromJson(body, Notification::class.java)

                        scope.launch { _notificationFlow.emit(notification) }

                        // Parse notification type and emit dashboard refresh event
                        val event = mapNotificationTypeToEvent(notification.type)
                        if (event != null) {
                            Log.d(TAG, "Emitting dashboard refresh event: $event")
                            scope.launch { _dashboardRefreshFlow.emit(event) }
                        }

                        appContext?.let { context ->
                            NotificationHelper.showNotification(context, notification)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse notification", e)
            }
        }
    }

    private fun mapNotificationTypeToEvent(type: String): RealTimeEvent? {
        return when (type.uppercase()) {
            "BOOKING_REQUEST" -> RealTimeEvent.BOOKING_RECEIVED
            "BOOKING_ACCEPTED" -> RealTimeEvent.BOOKING_ACCEPTED
            "BOOKING_REJECTED" -> RealTimeEvent.BOOKING_REJECTED
            "BOOKING_CANCELLED" -> RealTimeEvent.BOOKING_CANCELLED
            "RIDE_STARTED" -> RealTimeEvent.RIDE_UPDATED
            "RIDE_COMPLETED" -> RealTimeEvent.RIDE_UPDATED
            "RIDE_CANCELLED" -> RealTimeEvent.RIDE_CANCELLED
            "NEW_REVIEW" -> RealTimeEvent.NEW_REVIEW
            else -> RealTimeEvent.GENERAL_UPDATE
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User logout")
        isConnected = false
    }

    private const val TAG = "WebSocketManager"
}
