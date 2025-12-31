package com.tp.blassa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tp.blassa.core.network.Ride
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RideCard(ride: Ride, onClick: () -> Unit) {
        Card(
                modifier =
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable {
                                onClick()
                        },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(56.dp)
                                ) {
                                        Text(
                                                text = formatTime(ride.departureTime),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                        )
                                        Text(
                                                text = formatDate(ride.departureTime),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                        )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier = Modifier.width(20.dp)
                                                ) {
                                                        Box(
                                                                modifier =
                                                                        Modifier.size(10.dp)
                                                                                .background(
                                                                                        BlassaTeal,
                                                                                        CircleShape
                                                                                )
                                                        )
                                                        Box(
                                                                modifier =
                                                                        Modifier.width(2.dp)
                                                                                .height(28.dp)
                                                                                .background(
                                                                                        Brush.verticalGradient(
                                                                                                listOf(
                                                                                                        BlassaTeal,
                                                                                                        BlassaAmber
                                                                                                )
                                                                                        )
                                                                                )
                                                        )
                                                        Box(
                                                                modifier =
                                                                        Modifier.size(10.dp)
                                                                                .background(
                                                                                        BlassaAmber,
                                                                                        CircleShape
                                                                                )
                                                        )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                text = ride.originName,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                fontWeight = FontWeight.Medium,
                                                                color = TextPrimary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                        Text(
                                                                text = ride.destinationName,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                fontWeight = FontWeight.Medium,
                                                                color = TextPrimary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                        )
                                                }
                                        }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                                text = "${ride.pricePerSeat.toInt()}",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = BlassaTeal
                                        )
                                        Text(
                                                text = "TND",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                        )
                                }
                        }

                        HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFF1F5F9)
                        )

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                                modifier = Modifier.size(44.dp),
                                                contentAlignment = Alignment.BottomEnd
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(44.dp)
                                                                        .clip(CircleShape)
                                                                        .background(
                                                                                BlassaTeal.copy(
                                                                                        alpha = 0.1f
                                                                                )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        if (ride.driverProfilePictureUrl != null) {
                                                                AsyncImage(
                                                                        model =
                                                                                ride.driverProfilePictureUrl
                                                                                        .replace(
                                                                                                "=s96-c",
                                                                                                "=s200-c"
                                                                                        ),
                                                                        contentDescription =
                                                                                ride.driverName,
                                                                        modifier =
                                                                                Modifier.fillMaxSize()
                                                                                        .clip(
                                                                                                CircleShape
                                                                                        ),
                                                                        contentScale =
                                                                                ContentScale.Crop
                                                                )
                                                        } else {
                                                                Icon(
                                                                        Icons.Default.Person,
                                                                        contentDescription = null,
                                                                        tint = BlassaTeal,
                                                                        modifier =
                                                                                Modifier.size(24.dp)
                                                                )
                                                        }
                                                }
                                                Box(
                                                        modifier =
                                                                Modifier.size(18.dp)
                                                                        .background(
                                                                                Color(0xFF10B981),
                                                                                CircleShape
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Default.Shield,
                                                                contentDescription = "Verified",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(10.dp)
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                                Text(
                                                        text = ride.driverName ?: "Conducteur",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextPrimary
                                                )
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        if (ride.driverRating != null &&
                                                                        ride.driverRating > 0
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Star,
                                                                        contentDescription = null,
                                                                        tint = Color(0xFFFBBF24),
                                                                        modifier =
                                                                                Modifier.size(14.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(2.dp)
                                                                )
                                                                Text(
                                                                        text =
                                                                                String.format(
                                                                                        "%.1f",
                                                                                        ride.driverRating
                                                                                ),
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelMedium,
                                                                        fontWeight =
                                                                                FontWeight.Medium,
                                                                        color = Color(0xFFD97706)
                                                                )
                                                        } else {
                                                                Text(
                                                                        text = "Nouveau",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelSmall,
                                                                        color = TextSecondary
                                                                )
                                                        }
                                                        Text(
                                                                text = " · ",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = TextSecondary
                                                        )
                                                        Text(
                                                                text = "Vérifié",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = Color(0xFF10B981),
                                                                fontWeight = FontWeight.Medium
                                                        )
                                                }
                                        }
                                }

                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                        if (ride.allowsSmoking) {
                                                Surface(
                                                        color = Color(0xFFF1F5F9),
                                                        shape = RoundedCornerShape(6.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Default.SmokingRooms,
                                                                contentDescription =
                                                                        "Smoking allowed",
                                                                tint = TextSecondary,
                                                                modifier =
                                                                        Modifier.padding(4.dp)
                                                                                .size(14.dp)
                                                        )
                                                }
                                        }

                                        if (ride.genderPreference != "ANY") {
                                                val isWomen = ride.genderPreference == "FEMALE_ONLY"
                                                Surface(
                                                        color =
                                                                if (isWomen) Color(0xFFFDF2F8)
                                                                else Color(0xFFEFF6FF),
                                                        shape = RoundedCornerShape(6.dp),
                                                        border = null
                                                ) {
                                                        Text(
                                                                text =
                                                                        if (isWomen) "♀ Femmes"
                                                                        else "♂ Hommes",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color =
                                                                        if (isWomen)
                                                                                Color(0xFFBE185D)
                                                                        else Color(0xFF1D4ED8),
                                                                fontWeight = FontWeight.Medium,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 8.dp,
                                                                                vertical = 4.dp
                                                                        )
                                                        )
                                                }
                                        }

                                        Surface(
                                                color = Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(6.dp)
                                        ) {
                                                Row(
                                                        modifier =
                                                                Modifier.padding(
                                                                        horizontal = 8.dp,
                                                                        vertical = 4.dp
                                                                ),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                Icons.Default.EventSeat,
                                                                contentDescription = null,
                                                                tint = TextSecondary,
                                                                modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                                text = "${ride.availableSeats}",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelMedium,
                                                                color = TextSecondary
                                                        )
                                                }
                                        }

                                        Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color(0xFFCBD5E1),
                                                modifier = Modifier.size(20.dp)
                                        )
                                }
                        }
                }
        }
}

private fun formatTime(dateTime: String): String {
        return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateTime)
                date?.let { outputFormat.format(it) } ?: "--:--"
        } catch (e: Exception) {
                "--:--"
        }
}

private fun formatDate(dateTime: String): String {
        return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("d MMM", Locale.FRENCH)
                val date = inputFormat.parse(dateTime)
                date?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
                ""
        }
}
