package com.tp.blassa.features.rides.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.core.network.PassengerInfo
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun PassengerList(
        passengers: List<PassengerInfo>,
        onAccept: (String) -> Unit,
        onReject: (String) -> Unit,
        loadingBookingId: String? = null,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
                text = "Passagers (${passengers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
        )

        if (passengers.isEmpty()) {
            Text(
                    text = "Aucun passager pour le moment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                passengers.forEach { passenger ->
                    PassengerCard(
                            passenger = passenger,
                            onAccept = { onAccept(passenger.bookingId) },
                            onReject = { onReject(passenger.bookingId) },
                            isLoading = loadingBookingId == passenger.bookingId
                    )
                }
            }
        }
    }
}

@Composable
fun PassengerCard(
        passenger: PassengerInfo,
        onAccept: () -> Unit,
        onReject: () -> Unit,
        isLoading: Boolean,
        modifier: Modifier = Modifier
) {
    Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .clip(CircleShape)
                                        .background(BlassaTeal.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                ) {
                    Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BlassaTeal,
                            modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = passenger.passengerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                    )
                    Text(
                            text = "${passenger.seatsBooked} place(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                    )
                }

                // Status Badge
                StatusBadge(status = passenger.status)
            }

            // Action Buttons for Pending Requests
            if (passenger.status == "PENDING") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Reject Button
                    Button(
                            onClick = onReject,
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFEE2E2),
                                            contentColor = Color(0xFFDC2626)
                                    ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoading,
                            contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFFDC2626),
                                    strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refuser", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    // Accept Button
                    Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFDCFCE7),
                                            contentColor = Color(0xFF16A34A)
                                    ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoading,
                            contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF16A34A),
                                    strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Accepter", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (backgroundColor, textColor, text) =
            when (status) {
                "CONFIRMED" -> Triple(Color(0xFFDCFCE7), Color(0xFF16A34A), "Confirmé")
                "PENDING" -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "En attente")
                "REJECTED" -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Refusé")
                "CANCELLED" -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "Annulé")
                else -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), status)
            }

    Surface(color = backgroundColor, shape = RoundedCornerShape(4.dp)) {
        Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
