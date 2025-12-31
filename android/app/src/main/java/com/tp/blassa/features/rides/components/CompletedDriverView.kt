package com.tp.blassa.features.rides.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tp.blassa.core.network.PassengerInfo
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.ui.theme.BlassaTeal

@Composable
fun CompletedDriverView(
        ride: RideDetail,
        passengers: List<PassengerInfo>,
        reviewedPassengers: Set<String>,
        onSubmitReview: (bookingId: String, rating: Int, comment: String) -> Unit,
        onBackToHome: () -> Unit,
        onPublishNewRide: () -> Unit,
        isReviewSubmitting: String?, // booking ID that is currently submitting
        onPassengerProfileClick: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        val confirmedPassengers = passengers.filter { it.status == "CONFIRMED" }
        val totalEarnings = confirmedPassengers.sumOf { it.seatsBooked * ride.pricePerSeat }

        Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
                ) {
                        Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(80.dp)
                                                        .background(Color(0xFFD1FAE5), CircleShape),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(48.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                        text = "Trajet terminé !",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                        text =
                                                "Bravo pour ce covoiturage.\nVoici le récapitulatif de votre trajet.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF059669),
                                        textAlign = TextAlign.Center
                                )
                        }
                }

                Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Earnings Card
                        Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(48.dp)
                                                                .background(
                                                                        Color(0xFFFEF3C7),
                                                                        RoundedCornerShape(12.dp)
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        Icons.Default.Paid,
                                                        contentDescription = null,
                                                        tint = Color(0xFFD97706),
                                                        modifier = Modifier.size(24.dp)
                                                )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text = "$totalEarnings TND",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                                text = "Gain estimé",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                        )
                                }
                        }

                        // Passengers Card
                        Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(48.dp)
                                                                .background(
                                                                        Color(0xFFDBEAFE),
                                                                        RoundedCornerShape(12.dp)
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        Icons.Default.Groups,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2563EB),
                                                        modifier = Modifier.size(24.dp)
                                                )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text = "${confirmedPassengers.size}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                                text = "Passagers",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                        )
                                }
                        }
                }

                if (confirmedPassengers.isNotEmpty()) {
                        Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Notez vos passagers",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                                text = "Aidez la communauté en laissant un avis",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        confirmedPassengers.forEach { passenger ->
                                                PassengerReviewItem(
                                                        passenger = passenger,
                                                        isReviewed =
                                                                reviewedPassengers.contains(
                                                                        passenger.bookingId
                                                                ),
                                                        isSubmitting =
                                                                isReviewSubmitting ==
                                                                        passenger.bookingId,
                                                        onSubmitReview = { rating, comment ->
                                                                onSubmitReview(
                                                                        passenger.bookingId,
                                                                        rating,
                                                                        comment
                                                                )
                                                        },
                                                        onProfileClick = {
                                                                onPassengerProfileClick(
                                                                        passenger.passengerId
                                                                )
                                                        }
                                                )
                                                if (passenger != confirmedPassengers.last()) {
                                                        HorizontalDivider(
                                                                modifier =
                                                                        Modifier.padding(
                                                                                vertical = 12.dp
                                                                        ),
                                                                color = Color(0xFFF1F5F9)
                                                        )
                                                }
                                        }
                                }
                        }
                }

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        OutlinedButton(
                                onClick = onBackToHome,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color.Gray
                                        )
                        ) {
                                Icon(Icons.Default.Home, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Accueil", fontWeight = FontWeight.Medium)
                        }

                        Button(
                                onClick = onPublishNewRide,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                        ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publier", fontWeight = FontWeight.Bold)
                        }
                }
        }
}

@Composable
private fun PassengerReviewItem(
        passenger: PassengerInfo,
        isReviewed: Boolean,
        isSubmitting: Boolean,
        onSubmitReview: (rating: Int, comment: String) -> Unit,
        onProfileClick: () -> Unit
) {
        var rating by remember { mutableIntStateOf(0) }
        var comment by remember { mutableStateOf("") }
        var showCommentField by remember { mutableStateOf(false) }

        Column {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = onProfileClick)
                                        .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                        ) {
                                if (!passenger.passengerProfilePictureUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                                model = passenger.passengerProfilePictureUrl,
                                                contentDescription = passenger.passengerName,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                        )
                                } else {
                                        Text(
                                                text = passenger.passengerName.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Gray
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = passenger.passengerName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                if (isReviewed) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                        Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = Color(0xFF059669),
                                                        modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        text = "Avis envoyé",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF059669),
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                } else {
                                        Text(
                                                text = "Comment s'est passé le voyage ?",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                        )
                                }
                        }
                }

                if (!isReviewed) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.Start) {
                                (1..5).forEach { star ->
                                        IconButton(
                                                onClick = { rating = star },
                                                modifier = Modifier.size(36.dp)
                                        ) {
                                                Icon(
                                                        if (star <= rating) Icons.Default.Star
                                                        else Icons.Default.StarBorder,
                                                        contentDescription = "Note $star",
                                                        tint =
                                                                if (star <= rating)
                                                                        Color(0xFFFBBF24)
                                                                else Color(0xFFE2E8F0),
                                                        modifier = Modifier.size(28.dp)
                                                )
                                        }
                                }
                        }

                        if (showCommentField || comment.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                        value = comment,
                                        onValueChange = { comment = it },
                                        placeholder = {
                                                Text(
                                                        "Commentaire (optionnel)",
                                                        color = Color.Gray,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        minLines = 2,
                                        maxLines = 3,
                                        textStyle = MaterialTheme.typography.bodySmall,
                                        colors =
                                                OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = BlassaTeal,
                                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                                )
                                )
                        } else if (rating > 0) {
                                TextButton(onClick = { showCommentField = true }) {
                                        Text("+ Ajouter un commentaire", color = BlassaTeal)
                                }
                        }

                        if (rating > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                ) {
                                        Button(
                                                onClick = { onSubmitReview(rating, comment) },
                                                enabled = !isSubmitting,
                                                shape = RoundedCornerShape(8.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFF1F2937)
                                                        ),
                                                contentPadding =
                                                        PaddingValues(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        )
                                        ) {
                                                if (isSubmitting) {
                                                        CircularProgressIndicator(
                                                                color = Color.White,
                                                                modifier = Modifier.size(16.dp),
                                                                strokeWidth = 2.dp
                                                        )
                                                } else {
                                                        Text(
                                                                "Envoyer",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelMedium
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                                Icons.Default.Send,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(14.dp)
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
