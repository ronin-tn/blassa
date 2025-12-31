package com.tp.blassa.features.rides.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.core.network.PassengerInfo
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.ui.theme.BlassaTeal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DriverOwnRideView(
        ride: RideDetail,
        passengers: List<PassengerInfo>,
        isLoading: Boolean,
        onStartRide: () -> Unit,
        onCancelRide: () -> Unit,
        onCompleteRide: () -> Unit,
        onManageRides: () -> Unit,
        onAcceptBooking: (String) -> Unit,
        onRejectBooking: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .background(Color(0xFF10B981), CircleShape),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.DirectionsCar,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                        )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                        Text(
                                                text = "Votre trajet",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF166534)
                                        )
                                        Text(
                                                text = getStatusText(ride.status),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF16A34A)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        PassengerList(
                                passengers = passengers,
                                onAccept = onAcceptBooking,
                                onReject = onRejectBooking,
                                loadingBookingId = if (isLoading) "loading" else null
                        )

                        val confirmedCount = passengers.count { it.status == "CONFIRMED" }
                        if (ride.status == "SCHEDULED" && confirmedCount == 0) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color(0xFFFEF3C7)
                                                )
                                ) {
                                        Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.Top
                                        ) {
                                                Icon(
                                                        Icons.Default.HourglassEmpty,
                                                        contentDescription = null,
                                                        tint = Color(0xFFD97706),
                                                        modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                        Text(
                                                                text = "Phase de recherche",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = Color(0xFF92400E)
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                                text =
                                                                        "Votre trajet est visible. Attendez les demandes de réservation ou démarrez 30 min avant le départ.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = Color(0xFFB45309)
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        when (ride.status) {
                                "SCHEDULED" -> {
                                        // Start Ride Button
                                        Button(
                                                onClick = onStartRide,
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = BlassaTeal
                                                        ),
                                                enabled =
                                                        !isLoading &&
                                                                remember(
                                                                        ride.departureTime,
                                                                        passengers
                                                                ) {
                                                                        canStartRide(
                                                                                ride.departureTime,
                                                                                passengers
                                                                        )
                                                                }
                                        ) {
                                                if (isLoading) {
                                                        CircularProgressIndicator(
                                                                modifier = Modifier.size(20.dp),
                                                                color = Color.White,
                                                                strokeWidth = 2.dp
                                                        )
                                                } else {
                                                        Icon(
                                                                Icons.Default.PlayArrow,
                                                                contentDescription = null
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                "Démarrer le trajet",
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Cancel Button
                                        OutlinedButton(
                                                onClick = onCancelRide,
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.outlinedButtonColors(
                                                                contentColor = Color(0xFFDC2626)
                                                        ),
                                                enabled = !isLoading
                                        ) {
                                                Icon(
                                                        Icons.Default.Cancel,
                                                        contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        "Annuler le trajet",
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                }
                                "IN_PROGRESS" -> {

                                        Button(
                                                onClick = onCompleteRide,
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFF10B981)
                                                        ),
                                                enabled = !isLoading
                                        ) {
                                                Text(
                                                        "Terminer le trajet",
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                }
                                "COMPLETED" -> {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor = Color(0xFFD1FAE5)
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                ) {
                                                        Icon(
                                                                Icons.Default.CheckCircle,
                                                                contentDescription = null,
                                                                tint = Color(0xFF059669),
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                text = "Trajet terminé avec succès",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = Color(0xFF047857),
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier.fillMaxWidth()
                                                        )
                                                }
                                        }
                                }
                                else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = onManageRides, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                        text = "← Gérer mes trajets",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF16A34A)
                                )
                        }
                }
        }
}

private fun getStatusText(status: String): String {
        return when (status) {
                "SCHEDULED" -> "Programmé"
                "IN_PROGRESS" -> "En cours"
                "COMPLETED" -> "Terminé"
                "CANCELLED" -> "Annulé"
                else -> status
        }
}

private fun canStartRide(departureTime: String, passengers: List<PassengerInfo>): Boolean {
        val hasConfirmedPassengers = passengers.any { it.status == "CONFIRMED" }
        if (hasConfirmedPassengers) return true

        return isWithin30Minutes(departureTime)
}

private fun isWithin30Minutes(departureTimeStr: String): Boolean {
        return try {

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                val departureTime = LocalDateTime.parse(departureTimeStr, formatter)
                val now = LocalDateTime.now()

                val minutesUntil = ChronoUnit.MINUTES.between(now, departureTime)

                minutesUntil <= 30
        } catch (e: Exception) {

                false
        }
}
