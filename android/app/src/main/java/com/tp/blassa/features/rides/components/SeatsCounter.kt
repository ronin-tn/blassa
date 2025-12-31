package com.tp.blassa.features.rides.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.TextPrimary

@Composable
fun SeatsCounter(
        seats: Int,
        onSeatsChange: (Int) -> Unit,
        minSeats: Int = 1,
        maxSeats: Int = 8,
        modifier: Modifier = Modifier
) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
                IconButton(
                        onClick = { if (seats > minSeats) onSeatsChange(seats - 1) },
                        modifier =
                                Modifier.size(48.dp)
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                ) { Icon(Icons.Default.Remove, contentDescription = "Moins", tint = TextPrimary) }
                Text(
                        text = "$seats",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.width(64.dp),
                        textAlign = TextAlign.Center
                )
                IconButton(
                        onClick = { if (seats < maxSeats) onSeatsChange(seats + 1) },
                        modifier =
                                Modifier.size(48.dp)
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                ) { Icon(Icons.Default.Add, contentDescription = "Plus", tint = TextPrimary) }
        }
}
