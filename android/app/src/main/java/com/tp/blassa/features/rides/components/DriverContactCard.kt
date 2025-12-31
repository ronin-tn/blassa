package com.tp.blassa.features.rides.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun DriverContactCard(
        ride: RideDetail,
        onDriverProfileClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        val context = LocalContext.current

        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                                text = "Conducteur",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onDriverProfileClick() }
                                                .background(Color.White)
                                                .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(
                                        modifier = Modifier.size(56.dp),
                                        contentAlignment = Alignment.BottomEnd
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(56.dp)
                                                                .clip(CircleShape)
                                                                .background(BlassaTeal),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                if (ride.driverProfilePictureUrl != null) {
                                                        AsyncImage(
                                                                model =
                                                                        ride.driverProfilePictureUrl
                                                                                .replace(
                                                                                        "=s96-c",
                                                                                        "=s400-c"
                                                                                ),
                                                                contentDescription =
                                                                        ride.driverName,
                                                                modifier =
                                                                        Modifier.fillMaxSize()
                                                                                .clip(CircleShape),
                                                                contentScale = ContentScale.Crop
                                                        )
                                                } else {
                                                        Text(
                                                                text =
                                                                        ride.driverName
                                                                                .take(1)
                                                                                .uppercase(),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                        )
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                        text = ride.driverName,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                        color = Color(0xFFE2E8F0),
                                                        shape = RoundedCornerShape(8.dp)
                                                ) {
                                                        Text(
                                                                text = "Voir profil",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = TextSecondary,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 8.dp,
                                                                                vertical = 4.dp
                                                                        )
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (ride.driverRating != null &&
                                                                ride.driverRating > 0
                                                ) {
                                                        Icon(
                                                                Icons.Default.Star,
                                                                contentDescription = null,
                                                                tint = Color(0xFFFBBF24),
                                                                modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                                text =
                                                                        String.format(
                                                                                "%.1f",
                                                                                ride.driverRating
                                                                        ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = Color(0xFFD97706)
                                                        )
                                                } else {
                                                        Text(
                                                                text = "Nouveau conducteur",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = TextSecondary
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = "Contacter le conducteur",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ride.driverPhoneNumber?.let { phone ->
                                Button(
                                        onClick = {
                                                val intent =
                                                        Intent(
                                                                Intent.ACTION_DIAL,
                                                                Uri.parse("tel:$phone")
                                                        )
                                                context.startActivity(intent)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF10B981)
                                                )
                                ) {
                                        Icon(
                                                Icons.Default.Phone,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                                Text(
                                                        text = phone,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                        text = "Appuyer pour appeler",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White.copy(alpha = 0.8f)
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                // Facebook Button
                                ride.driverFacebookUrl?.let { fbUrl ->
                                        Button(
                                                onClick = {
                                                        val intent =
                                                                Intent(
                                                                        Intent.ACTION_VIEW,
                                                                        Uri.parse(fbUrl)
                                                                )
                                                        context.startActivity(intent)
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFF1877F2)
                                                        )
                                        ) { Text("Facebook", fontWeight = FontWeight.SemiBold) }
                                }

                                // Instagram Button
                                ride.driverInstagramUrl?.let { igUrl ->
                                        Button(
                                                onClick = {
                                                        val intent =
                                                                Intent(
                                                                        Intent.ACTION_VIEW,
                                                                        Uri.parse(igUrl)
                                                                )
                                                        context.startActivity(intent)
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFFE4405F)
                                                        )
                                        ) { Text("Instagram", fontWeight = FontWeight.SemiBold) }
                                }
                        }
                }
        }
}
