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
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.ui.theme.BlassaTeal

@Composable
fun CompletedPassengerView(
        ride: RideDetail,
        bookingId: String,
        onSubmitReview: (bookingId: String, rating: Int, comment: String) -> Unit,
        onBackToHome: () -> Unit,
        isReviewSubmitting: Boolean,
        isReviewSubmitted: Boolean,
        hasExistingReview: Boolean,
        onDriverProfileClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        var rating by remember { mutableIntStateOf(0) }
        var comment by remember { mutableStateOf("") }

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
                                        text = "Vous êtes bien arrivé !",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                        text =
                                                "Merci d'avoir voyagé avec Blassa.\nNous espérons que votre trajet s'est bien passé.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF059669),
                                        textAlign = TextAlign.Center
                                )
                        }
                }

                Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                        text = "Notez votre conducteur",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                        text = "Votre avis compte pour la communauté",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable(onClick = onDriverProfileClick)
                                                        .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(72.dp)
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
                                                                                .headlineMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                                Text(
                                                        text = ride.driverName,
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold
                                                )
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                Icons.Default.Verified,
                                                                contentDescription = null,
                                                                tint = Color(0xFF059669),
                                                                modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                                text = "CONDUCTEUR",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = Color(0xFF059669),
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                if (isReviewSubmitted || hasExistingReview) {
                                        // Already reviewed message
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor = Color(0xFFECFDF5)
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                Icons.Default.CheckCircle,
                                                                contentDescription = null,
                                                                tint = Color(0xFF059669),
                                                                modifier = Modifier.size(32.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Column {
                                                                Text(
                                                                        text =
                                                                                if (hasExistingReview
                                                                                )
                                                                                        "Avis déjà envoyé"
                                                                                else
                                                                                        "Merci pour votre avis !",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        fontWeight =
                                                                                FontWeight.Bold,
                                                                        color = Color(0xFF047857)
                                                                )
                                                                Text(
                                                                        text =
                                                                                "Votre retour a bien été pris en compte.",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color = Color(0xFF059669)
                                                                )
                                                        }
                                                }
                                        }
                                } else {
                                        // Star Rating
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center
                                        ) {
                                                (1..5).forEach { star ->
                                                        IconButton(onClick = { rating = star }) {
                                                                Icon(
                                                                        if (star <= rating)
                                                                                Icons.Default.Star
                                                                        else
                                                                                Icons.Default
                                                                                        .StarBorder,
                                                                        contentDescription =
                                                                                "Note $star",
                                                                        tint =
                                                                                if (star <= rating)
                                                                                        Color(
                                                                                                0xFFFBBF24
                                                                                        )
                                                                                else
                                                                                        Color(
                                                                                                0xFFE2E8F0
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.size(40.dp)
                                                                )
                                                        }
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Comment Field
                                        OutlinedTextField(
                                                value = comment,
                                                onValueChange = { comment = it },
                                                placeholder = {
                                                        Text(
                                                                "Un commentaire sur la conduite de ${ride.driverName.split(" ")[0]} ? (optionnel)",
                                                                color = Color.Gray
                                                        )
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                minLines = 2,
                                                maxLines = 4,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = BlassaTeal,
                                                                unfocusedBorderColor =
                                                                        Color(0xFFE2E8F0)
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Submit Button
                                        Button(
                                                onClick = {
                                                        onSubmitReview(bookingId, rating, comment)
                                                },
                                                enabled = rating > 0 && !isReviewSubmitting,
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = BlassaTeal,
                                                                disabledContainerColor =
                                                                        Color(0xFFE2E8F0)
                                                        ),
                                                shape = RoundedCornerShape(12.dp)
                                        ) {
                                                if (isReviewSubmitting) {
                                                        CircularProgressIndicator(
                                                                color = Color.White,
                                                                modifier = Modifier.size(20.dp),
                                                                strokeWidth = 2.dp
                                                        )
                                                } else {
                                                        Icon(
                                                                Icons.Default.Send,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                "Envoyer mon avis",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                }
                        }
                }

                // Back to Home Button
                OutlinedButton(
                        onClick = onBackToHome,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retour à l'accueil", fontWeight = FontWeight.Medium)
                }
        }
}
