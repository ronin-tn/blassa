package com.tp.blassa.features.rides.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.core.network.PassengerInfo
import com.tp.blassa.core.network.RideDetail

@Composable
fun ActiveDriverView(
        ride: RideDetail,
        passengers: List<PassengerInfo>,
        onComplete: () -> Unit,
        onCancel: () -> Unit,
        isLoading: Boolean,
        modifier: Modifier = Modifier
) {

        val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
        val alpha by
                infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(1000),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "alpha"
                )

        Column(modifier = modifier) {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                        Row(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(48.dp)
                                                        .background(
                                                                Color(0xFF10B981)
                                                                        .copy(alpha = alpha * 0.2f),
                                                                CircleShape
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.Navigation,
                                                contentDescription = "En route",
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(24.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                        Text(
                                                text = "Trajet en cours",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF047857)
                                        )
                                        Text(
                                                text = "Conduisez prudemment",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF059669)
                                        )
                                }
                        }
                }

                val confirmedPassengers = passengers.filter { it.status == "CONFIRMED" }

                if (confirmedPassengers.isNotEmpty()) {
                        Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Passagers à bord",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 12.dp)
                                        )
                                        confirmedPassengers.forEach { p ->
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                ) {
                                                        Box(
                                                                modifier =
                                                                        Modifier.size(32.dp)
                                                                                .background(
                                                                                        Color(
                                                                                                0xFFE2E8F0
                                                                                        ),
                                                                                        CircleShape
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                p.passengerName
                                                                                        .take(1)
                                                                                        .uppercase(),
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                        }
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                                p.passengerName,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge
                                                        )
                                                }
                                        }
                                }
                        }
                }

                Button(
                        onClick = onComplete,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(12.dp)
                ) {
                        if (isLoading) {
                                CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                )
                        } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        "Terminer le trajet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                }
        }
}
