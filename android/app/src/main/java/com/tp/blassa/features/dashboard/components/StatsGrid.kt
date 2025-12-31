package com.tp.blassa.features.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.features.dashboard.viewmodel.DashboardStats
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun StatsGrid(stats: DashboardStats, onStatClick: (String) -> Unit, modifier: Modifier = Modifier) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.DirectionsCar,
                                iconColor = BlassaTeal,
                                value = "${stats.totalTrips}",
                                label = "Trajets",
                                onClick = { onStatClick("trips") }
                        )
                        StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.DirectionsCar,
                                iconColor = BlassaAmber,
                                value = "${stats.totalRides}",
                                label = "Publiés",
                                onClick = { onStatClick("rides") }
                        )
                }
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.TrendingUp,
                                iconColor = Color(0xFF10B981),
                                value = "${stats.earnings.toInt()} TND",
                                label = "Gagnés",
                                onClick = { onStatClick("earnings") }
                        )
                        StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Star,
                                iconColor = Color(0xFFEAB308),
                                value = if (stats.rating > 0) "${stats.rating}/5" else "–",
                                label = "Note",
                                onClick = { onStatClick("reviews") }
                        )
                }
        }
}

@Composable
fun StatCard(
        icon: ImageVector,
        iconColor: Color,
        value: String,
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(44.dp)
                                                .background(
                                                        iconColor.copy(alpha = 0.1f),
                                                        CircleShape
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(22.dp)
                                )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                                Text(
                                        text = value,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                )
                                Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                )
                        }
                }
        }
}
