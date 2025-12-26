package com.example.blassa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blassa.R
import com.example.blassa.ui.theme.BlassaCyan
import com.example.blassa.ui.theme.BlassaYellow

data class NotificationItem(
    val title: String,
    val description: String, // Or styled text logic
    val timestamp: String,
    val type: NotificationType,
    val isUnread: Boolean = false
)

enum class NotificationType {
    RESERVATION_REQUEST,
    RESERVATION_CONFIRMED,
    REVIEW_RECEIVED,
    TRIP_CANCELLED
}

@Composable
fun NotificationsScreen() {
    val notifications = listOf(
        NotificationItem(
            "Nouvelle demande de réservation",
            "Ahmed souhaite réserver pour Tunis -> Sousse.",
            "il y a 10 min",
            NotificationType.RESERVATION_REQUEST,
            isUnread = true
        ),
        NotificationItem(
            "Réservation confirmée",
            "Votre trajet vers Bizerte est validé. Bon voyage !",
            "il y a 2 heures",
            NotificationType.RESERVATION_CONFIRMED,
             isUnread = true
        ),
        NotificationItem(
            "Nouvel avis reçu",
            "Sarah a laissé 5 étoiles : \"Trajet parfait, conducteur très sympa !\"",
            "Hier",
            NotificationType.REVIEW_RECEIVED
        ),
        NotificationItem(
            "Trajet annulé",
            "Le trajet de demain a été annulé par le conducteur. Vous avez été remboursé.",
            "il y a 2 jours",
            NotificationType.TRIP_CANCELLED
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA)) // Light Cyan/Blue background gradient logic, simplified to solid color or gradient background
    ) {
        // We'll use a Box to simulate the gradient background if needed, or just solid color from mockup seems light blueish
         Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F2F1), // Very light teal
                            Color(0xFFF5F5F5)  // White/Grey
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E)
                    )
                    TextButton(
                        onClick = { /* Todo */ },
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Tout marquer comme lu",
                            color = BlassaCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationCard(notification)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when (notification.type) {
                            NotificationType.RESERVATION_REQUEST -> Color(0xFFE0F2F1) // Light Cyan
                            NotificationType.RESERVATION_CONFIRMED -> Color(0xFFE8F5E9) // Light Green
                            NotificationType.REVIEW_RECEIVED -> Color(0xFFFFF8E1) // Light Yellow
                            NotificationType.TRIP_CANCELLED -> Color(0xFFFFEBEE) // Light Red
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                 // Icons - using placeholders or generic vectors
                 when (notification.type) {
                    NotificationType.RESERVATION_REQUEST -> Icon(painterResource(android.R.drawable.ic_menu_directions), contentDescription=null, tint=BlassaCyan) // Car icon placeholder
                    NotificationType.RESERVATION_CONFIRMED -> Icon(painterResource(android.R.drawable.ic_media_play), contentDescription=null, tint=Color(0xFF2E7D32)) // Ticket placeholder
                    NotificationType.REVIEW_RECEIVED -> Icon(Icons.Filled.Star, contentDescription=null, tint=Color(0xFFF9A825))
                    NotificationType.TRIP_CANCELLED -> Icon(painterResource(android.R.drawable.ic_delete), contentDescription=null, tint=Color(0xFFC62828)) // Warning placeholder
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E1E1E),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    if (notification.isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BlassaCyan, CircleShape)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Description with partial styling if needed (e.g. bold names)
                // Simplified for now
                Text(
                    text = notification.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = notification.timestamp,
                    fontSize = 12.sp,
                    color = BlassaCyan, // Or dynamically colored based on type
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    com.example.blassa.ui.theme.BlassaTheme {
        NotificationsScreen()
    }
}
