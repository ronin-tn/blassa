package com.tp.blassa.features.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.features.dashboard.viewmodel.UpcomingRide
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RideCard(ride: UpcomingRide, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Card(
                modifier =
                        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable {
                                onClick()
                        },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .background(
                                                        if (ride.type == "driver")
                                                                BlassaTeal.copy(alpha = 0.1f)
                                                        else BlassaAmber.copy(alpha = 0.1f),
                                                        CircleShape
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint =
                                                if (ride.type == "driver") BlassaTeal
                                                else BlassaAmber,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = "${ride.origin} → ${ride.destination}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1
                                )
                                Text(
                                        text = formatDateTime(ride.departureTime),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                                Text(
                                        text = "${ride.price.toInt()} TND",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                        color =
                                                (if (ride.type == "driver") BlassaTeal
                                                        else BlassaAmber)
                                                        .copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                ) {
                                        Text(
                                                text =
                                                        if (ride.type == "driver") "Conducteur"
                                                        else "Passager",
                                                style = MaterialTheme.typography.labelSmall,
                                                color =
                                                        if (ride.type == "driver") BlassaTeal
                                                        else BlassaAmber,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 8.dp,
                                                                vertical = 2.dp
                                                        )
                                        )
                                }
                        }
                }
        }
}

private fun formatDateTime(dateTime: String): String {
        return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEE d MMM, HH:mm", Locale.FRENCH)
                inputFormat.parse(dateTime)?.let { outputFormat.format(it) } ?: dateTime
        } catch (e: Exception) {
                dateTime
        }
}
