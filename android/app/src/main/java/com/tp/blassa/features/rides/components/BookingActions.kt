package com.tp.blassa.features.rides.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun BookingActions(
        ride: RideDetail,
        onBook: (Int) -> Unit,
        isBookingLoading: Boolean,
        modifier: Modifier = Modifier
) {
        var selectedSeats by remember { mutableIntStateOf(1) }

        val maxSeats = minOf(4, ride.availableSeats)

        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
                Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        imageVector = Icons.Default.ConfirmationNumber,
                                        contentDescription = null,
                                        tint = BlassaTeal,
                                        modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = "Réserver votre place",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "Nombre de places",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                SeatDataButton(
                                        icon = Icons.Default.Remove,
                                        enabled = selectedSeats > 1 && !isBookingLoading,
                                        onClick = { if (selectedSeats > 1) selectedSeats-- }
                                )

                                Text(
                                        text = "$selectedSeats",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(48.dp),
                                        textAlign = TextAlign.Center,
                                        color = TextPrimary
                                )

                                SeatDataButton(
                                        icon = Icons.Default.Add,
                                        enabled = selectedSeats < maxSeats && !isBookingLoading,
                                        onClick = { if (selectedSeats < maxSeats) selectedSeats++ }
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                        ) {
                                Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                                text =
                                                        String.format(
                                                                "%.2f",
                                                                ride.pricePerSeat * selectedSeats
                                                        ),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = BlassaTeal
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = "TND",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = BlassaTeal,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                                onClick = { onBook(selectedSeats) },
                                enabled = !isBookingLoading && maxSeats > 0,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = BlassaTeal,
                                                contentColor = Color.White
                                        ),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                                if (isBookingLoading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                        )
                                } else {
                                        Text(
                                                text = "Confirmer la réservation",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun SeatDataButton(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        enabled: Boolean,
        onClick: () -> Unit
) {
        Box(
                modifier =
                        Modifier.size(40.dp)
                                .border(
                                        width = 1.dp,
                                        color = Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                        color =
                                                if (enabled) Color.Transparent
                                                else Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(12.dp)
                                )
                                .shimmerClickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) Color(0xFF475569) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                )
        }
}

@Composable
private fun Modifier.shimmerClickable(enabled: Boolean, onClick: () -> Unit): Modifier {
        return this.then(
                if (enabled) {
                        Modifier.clickable(onClick = onClick)
                } else {
                        Modifier
                }
        )
}
