package com.tp.blassa.features.rides.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun ActivePassengerView(
        ride: RideDetail,
        onDriverProfileClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        val context = LocalContext.current

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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
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
                                                text = "En route",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF047857)
                                        )
                                        Text(
                                                text = "Vers ${ride.destinationName}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF059669)
                                        )
                                }
                        }
                }

                Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                        text = "VOTRE CONDUCTEUR",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable(onClick = onDriverProfileClick)
                                                        .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(56.dp)
                                                                .clip(CircleShape)
                                                                .background(BlassaTeal),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                if (!ride.driverProfilePictureUrl.isNullOrEmpty()) {
                                                        AsyncImage(
                                                                model =
                                                                        ride.driverProfilePictureUrl,
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

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        text = ride.driverName,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                )
                                                if (ride.driverRating != null &&
                                                                ride.driverRating > 0
                                                ) {
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Star,
                                                                        contentDescription = null,
                                                                        tint = Color(0xFFFBBF24),
                                                                        modifier =
                                                                                Modifier.size(16.dp)
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
                                                                                        .bodyMedium,
                                                                        color = Color.Gray
                                                                )
                                                        }
                                                }
                                        }

                                        Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = "Voir profil",
                                                tint = Color.Gray
                                        )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                                FilledIconButton(
                                                        onClick = {
                                                                val intent =
                                                                        Intent(Intent.ACTION_DIAL)
                                                                                .apply {
                                                                                        data =
                                                                                                Uri.parse(
                                                                                                        "tel:${ride.driverPhoneNumber}"
                                                                                                )
                                                                                }
                                                                context.startActivity(intent)
                                                        },
                                                        modifier = Modifier.size(48.dp),
                                                        colors =
                                                                IconButtonDefaults
                                                                        .filledIconButtonColors(
                                                                                containerColor =
                                                                                        Color(
                                                                                                0xFFECFDF5
                                                                                        ),
                                                                                contentColor =
                                                                                        Color(
                                                                                                0xFF059669
                                                                                        )
                                                                        )
                                                ) {
                                                        Icon(
                                                                Icons.Default.Phone,
                                                                contentDescription = "Appeler",
                                                                modifier = Modifier.size(24.dp)
                                                        )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        "Appeler",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.Gray
                                                )
                                        }

                                        if (!ride.driverFacebookUrl.isNullOrEmpty()) {
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier =
                                                                Modifier.padding(horizontal = 16.dp)
                                                ) {
                                                        FilledIconButton(
                                                                onClick = {
                                                                        val intent =
                                                                                Intent(
                                                                                        Intent.ACTION_VIEW,
                                                                                        Uri.parse(
                                                                                                ride.driverFacebookUrl
                                                                                        )
                                                                                )
                                                                        context.startActivity(
                                                                                intent
                                                                        )
                                                                },
                                                                modifier = Modifier.size(48.dp),
                                                                colors =
                                                                        IconButtonDefaults
                                                                                .filledIconButtonColors(
                                                                                        containerColor =
                                                                                                Color(
                                                                                                        0xFFE7F3FF
                                                                                                ),
                                                                                        contentColor =
                                                                                                Color(
                                                                                                        0xFF1877F2
                                                                                                )
                                                                                )
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Facebook,
                                                                        contentDescription =
                                                                                "Facebook",
                                                                        modifier =
                                                                                Modifier.size(24.dp)
                                                                )
                                                        }
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                                "Facebook",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = Color.Gray
                                                        )
                                                }
                                        }

                                        if (!ride.driverInstagramUrl.isNullOrEmpty()) {
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier =
                                                                Modifier.padding(horizontal = 16.dp)
                                                ) {
                                                        FilledIconButton(
                                                                onClick = {
                                                                        val intent =
                                                                                Intent(
                                                                                        Intent.ACTION_VIEW,
                                                                                        Uri.parse(
                                                                                                ride.driverInstagramUrl
                                                                                        )
                                                                                )
                                                                        context.startActivity(
                                                                                intent
                                                                        )
                                                                },
                                                                modifier = Modifier.size(48.dp),
                                                                colors =
                                                                        IconButtonDefaults
                                                                                .filledIconButtonColors(
                                                                                        containerColor =
                                                                                                Color(
                                                                                                        0xFFFCE7F3
                                                                                                ),
                                                                                        contentColor =
                                                                                                Color(
                                                                                                        0xFFE4405F
                                                                                                )
                                                                                )
                                                        ) {
                                                                // Instagram icon from Material
                                                                // Extended Icons
                                                                Icon(
                                                                        Icons.Default.PhotoCamera,
                                                                        contentDescription =
                                                                                "Instagram",
                                                                        modifier =
                                                                                Modifier.size(24.dp)
                                                                )
                                                        }
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                                "Instagram",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = Color.Gray
                                                        )
                                                }
                                        }
                                }
                        }
                }

                if (!ride.carMake.isNullOrEmpty()) {
                        Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "VÉHICULE",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(48.dp)
                                                                        .background(
                                                                                Color(0xFFF1F5F9),
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Default.DirectionsCar,
                                                                contentDescription = null,
                                                                tint = BlassaTeal,
                                                                modifier = Modifier.size(24.dp)
                                                        )
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column {
                                                        Text(
                                                                text =
                                                                        "${ride.carMake} ${ride.carModel ?: ""}",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                        if (!ride.carColor.isNullOrEmpty()) {
                                                                Text(
                                                                        text = ride.carColor,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        color = Color.Gray
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }

                Button(
                        onClick = {
                                val gmmIntentUri =
                                        Uri.parse(
                                                "google.navigation:q=${ride.destinationLat},${ride.destinationLon}"
                                        )
                                val mapIntent =
                                        Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                                setPackage("com.google.android.apps.maps")
                                        }
                                context.startActivity(mapIntent)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal),
                        shape = RoundedCornerShape(12.dp)
                ) {
                        Icon(Icons.Default.Navigation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                "Suivre le trajet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )
                }
        }
}
