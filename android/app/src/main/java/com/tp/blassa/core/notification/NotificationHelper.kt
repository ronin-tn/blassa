package com.tp.blassa.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tp.blassa.MainActivity
import com.tp.blassa.R
import com.tp.blassa.core.network.Notification

object NotificationHelper {

    private const val CHANNEL_ID = "blassa_notifications"
    private const val CHANNEL_NAME = "Blassa Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for ride bookings and updates"

    private var notificationId = 0

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                        description = CHANNEL_DESCRIPTION
                        enableLights(true)
                        enableVibration(true)
                    }

            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, notification: Notification) {
        showNotification(
                context = context,
                title = getTitleFromType(notification.type),
                message = notification.message,
                notificationType = notification.type
        )
    }

    fun showNotification(
            context: Context,
            title: String,
            message: String,
            notificationType: String? = null
    ) {

        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    notificationType?.let { putExtra("notification_type", it) }
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

        val builder =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)

        try {
            NotificationManagerCompat.from(context).notify(notificationId++, builder.build())
        } catch (e: SecurityException) {

            android.util.Log.w("NotificationHelper", "Notification permission not granted", e)
        }
    }

    private fun getTitleFromType(type: String): String {
        return when (type) {
            "BOOKING_REQUEST" -> "Nouvelle demande de réservation"
            "BOOKING_CONFIRMED" -> "Réservation confirmée"
            "BOOKING_REJECTED" -> "Réservation refusée"
            "BOOKING_CANCELLED" -> "Réservation annulée"
            "RIDE_STARTED" -> "Trajet démarré"
            "RIDE_COMPLETED" -> "Trajet terminé"
            "RIDE_CANCELLED" -> "Trajet annulé"
            "NEW_REVIEW" -> "Nouvel avis"
            else -> "Blassa"
        }
    }
}
