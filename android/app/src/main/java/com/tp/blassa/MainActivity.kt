package com.tp.blassa

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.core.network.WebSocketManager
import com.tp.blassa.core.notification.NotificationHelper
import com.tp.blassa.navigation.BlassaNavHost
import com.tp.blassa.ui.theme.BlassaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        NotificationHelper.createNotificationChannel(this)
        WebSocketManager.init(this)
        requestNotificationPermission()

        enableEdgeToEdge()
        setContent { BlassaTheme { BlassaNavHost() } }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1001
    }
}
