package com.tp.blassa.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import com.tp.blassa.core.network.PublicProfileResponse
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.Review
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(userId: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
        var isLoading by remember { mutableStateOf(true) }
        var profile by remember { mutableStateOf<PublicProfileResponse?>(null) }
        var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(userId) {
                try {
                        isLoading = true
                        error = null

                        profile = RetrofitClient.dashboardApiService.getPublicProfile(userId)

                        try {
                                val reviewsResponse =
                                        RetrofitClient.dashboardApiService.getUserReviews(userId)
                                reviews = reviewsResponse.content
                        } catch (e: Exception) {

                                reviews = emptyList()
                        }

                        isLoading = false
                } catch (e: Exception) {
                        error = "Impossible de charger le profil"
                        isLoading = false
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Profil", fontWeight = FontWeight.SemiBold) },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Retour",
                                                        tint = Color.Black
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White,
                                                titleContentColor = TextPrimary
                                        )
                        )
                },
                containerColor = Color(0xFFF8FAFC),
                modifier = modifier
        ) { padding ->
                when {
                        isLoading -> {
                                Box(
                                        modifier = Modifier.fillMaxSize().padding(padding),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = BlassaTeal) }
                        }
                        error != null -> {
                                Box(
                                        modifier = Modifier.fillMaxSize().padding(padding),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                        error!!,
                                                        color = Color.Red,
                                                        textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Button(onClick = onBack) { Text("Retour") }
                                        }
                                }
                        }
                        profile != null -> {
                                ProfileContent(
                                        profile = profile!!,
                                        reviews = reviews,
                                        padding = padding
                                )
                        }
                }
        }
}

@Composable
private fun ProfileContent(
        profile: PublicProfileResponse,
        reviews: List<Review>,
        padding: PaddingValues,
        modifier: Modifier = Modifier
) {
        LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                item {
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.BottomEnd
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(120.dp)
                                                        .clip(CircleShape)
                                                        .background(BlassaTeal),
                                        contentAlignment = Alignment.Center
                                ) {
                                        if (!profile.profilePictureUrl.isNullOrEmpty()) {
                                                AsyncImage(
                                                        model =
                                                                profile.profilePictureUrl.replace(
                                                                        "=s96-c",
                                                                        "=s400-c"
                                                                ),
                                                        contentDescription =
                                                                "${profile.firstName} ${profile.lastName}",
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                )
                                        } else {
                                                Icon(
                                                        Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(60.dp)
                                                )
                                        }
                                }

                                Box(
                                        modifier =
                                                Modifier.size(32.dp)
                                                        .background(Color(0xFF10B981), CircleShape),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.Shield,
                                                contentDescription = "Vérifié",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text =
                                        "${profile.firstName} ${profile.lastName}".trim().ifEmpty {
                                                "Conducteur"
                                        },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                val rating = profile.averageRating
                                val ridesCount = profile.completedRidesCount ?: 0

                                if (rating != null && rating > 0) {
                                        Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = String.format("%.1f", rating),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFD97706)
                                        )
                                } else {

                                        val driverLabel =
                                                if (ridesCount < 5) "Nouveau conducteur"
                                                else "Conducteur expérimenté"
                                        Text(
                                                text = driverLabel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                }
                                Text(" · ", color = TextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = "Profil vérifié",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF10B981)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                StatCard(
                                        icon = Icons.Default.DirectionsCar,
                                        value = "${profile.completedRidesCount ?: 0}",
                                        label = "Trajets effectués",
                                        modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                        icon = Icons.Default.PersonOutline,
                                        value = formatMemberSince(profile.memberSince),
                                        label = "Membre depuis",
                                        modifier = Modifier.weight(1f)
                                )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                                text = "Avis (${reviews.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                }

                if (reviews.isEmpty()) {
                        item {
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                )
                                ) {
                                        Column(
                                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                                Text(
                                                        text = "Aucun avis pour le moment",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary,
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                } else {
                        items(reviews) { review ->
                                ReviewCard(review = review)
                                Spacer(modifier = Modifier.height(12.dp))
                        }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
        }
}

private fun formatMemberSince(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"

        return try {
                val date = LocalDate.parse(dateString)
                val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)
                date.format(formatter).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
                dateString.take(4).ifEmpty { "N/A" }
        }
}

@Composable
private fun ReviewCard(review: Review) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(BlassaTeal.copy(alpha = 0.2f)),
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
                                                text = review.reviewerName ?: "Anonyme",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary
                                        )

                                        Row {
                                                repeat(5) { index ->
                                                        Icon(
                                                                Icons.Default.Star,
                                                                contentDescription = null,
                                                                tint =
                                                                        if (index < review.rating)
                                                                                Color(0xFFFBBF24)
                                                                        else Color(0xFFE5E7EB),
                                                                modifier = Modifier.size(14.dp)
                                                        )
                                                }
                                        }
                                }
                        }

                        if (!review.comment.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                        text = review.comment,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                )
                        }
                }
        }
}

@Composable
private fun StatCard(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        value: String,
        label: String,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = BlassaTeal,
                                modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = value,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                        )
                        Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                        )
                }
        }
}
