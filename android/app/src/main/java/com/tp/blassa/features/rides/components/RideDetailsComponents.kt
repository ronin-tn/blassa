package com.tp.blassa.features.rides.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tp.blassa.core.network.RideDetail
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RouteHeaderCard(ride: RideDetail, modifier: Modifier = Modifier) {
        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        TimePriceRow(
                                departureTime = ride.departureTime,
                                pricePerSeat = ride.pricePerSeat
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        RouteVisualization(
                                originName = ride.originName,
                                destinationName = ride.destinationName
                        )

                        HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFF1F5F9)
                        )

                        RideBadgesRow(
                                availableSeats = ride.availableSeats,
                                allowsSmoking = ride.allowsSmoking,
                                allowsMusic = ride.allowsMusic,
                                allowsPets = ride.allowsPets,
                                luggageSize = ride.luggageSize,
                                genderPreference = ride.genderPreference
                        )
                }
        }
}

@Composable
private fun TimePriceRow(
        departureTime: String,
        pricePerSeat: Double,
        modifier: Modifier = Modifier
) {
        Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
        ) {
                Column {
                        Text(
                                text = formatTime(departureTime),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )
                        Text(
                                text = formatFullDate(departureTime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                        )
                }
                Column(horizontalAlignment = Alignment.End) {
                        Text(
                                text = "${pricePerSeat.toInt()} TND",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = BlassaTeal
                        )
                        Text(
                                text = "par place",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                        )
                }
        }
}

@Composable
private fun RouteVisualization(
        originName: String,
        destinationName: String,
        modifier: Modifier = Modifier
) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(24.dp)
                ) {
                        Box(modifier = Modifier.size(12.dp).background(BlassaTeal, CircleShape))
                        Box(
                                modifier =
                                        Modifier.width(3.dp)
                                                .height(48.dp)
                                                .background(
                                                        Brush.verticalGradient(
                                                                listOf(BlassaTeal, BlassaAmber)
                                                        )
                                                )
                        )
                        Box(modifier = Modifier.size(12.dp).background(BlassaAmber, CircleShape))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                        Text(
                                text = originName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                                text = destinationName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                        )
                }
        }
}

@Composable
private fun RideBadgesRow(
        availableSeats: Int,
        allowsSmoking: Boolean,
        allowsMusic: Boolean?,
        allowsPets: Boolean?,
        luggageSize: String?,
        genderPreference: String,
        modifier: Modifier = Modifier
) {
        FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
        ) {
                SeatsBadge(availableSeats = availableSeats)

                LuggageBadge(luggageSize = luggageSize ?: "MEDIUM")

                if (allowsSmoking) {
                        SmokingBadge()
                }

                if (allowsMusic == true) {
                        MusicBadge()
                }

                if (allowsPets == true) {
                        PetsBadge()
                }

                if (genderPreference != "ANY") {
                        GenderBadge(genderPreference = genderPreference)
                }
        }
}

@Composable
fun SeatsBadge(availableSeats: Int, modifier: Modifier = Modifier) {
        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp), modifier = modifier) {
                Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                Icons.Default.EventSeat,
                                contentDescription = null,
                                tint = BlassaTeal,
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = "$availableSeats places",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BlassaTeal
                        )
                }
        }
}

@Composable
fun SmokingBadge(modifier: Modifier = Modifier) {
        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp), modifier = modifier) {
                Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                Icons.Default.SmokingRooms,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = "Fumeur",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFD97706)
                        )
                }
        }
}

@Composable
fun GenderBadge(genderPreference: String, modifier: Modifier = Modifier) {
        val isWomen = genderPreference == "FEMALE_ONLY"
        Surface(
                color = if (isWomen) Color(0xFFFDF2F8) else Color(0xFFEFF6FF),
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
        ) {
                Text(
                        text = if (isWomen) "♀ Femmes uniquement" else "♂ Hommes uniquement",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (isWomen) Color(0xFFBE185D) else Color(0xFF1D4ED8),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
        }
}

@Composable
fun MusicBadge(modifier: Modifier = Modifier) {
        Surface(color = Color(0xFFF0FDF4), shape = RoundedCornerShape(8.dp), modifier = modifier) {
                Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = "Musique",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF16A34A)
                        )
                }
        }
}

@Composable
fun PetsBadge(modifier: Modifier = Modifier) {
        Surface(color = Color(0xFFFDF4FF), shape = RoundedCornerShape(8.dp), modifier = modifier) {
                Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                Icons.Default.Pets,
                                contentDescription = null,
                                tint = Color(0xFFC026D3),
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = "Animaux",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFC026D3)
                        )
                }
        }
}

@Composable
fun LuggageBadge(luggageSize: String, modifier: Modifier = Modifier) {
        val label =
                when (luggageSize) {
                        "SMALL" -> "Petit bagage"
                        "LARGE" -> "Grand bagage"
                        else -> "Bagage moyen"
                }
        Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp), modifier = modifier) {
                Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                Icons.Default.Work,
                                contentDescription = null,
                                tint = Color(0xFF4B5563),
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF4B5563)
                        )
                }
        }
}

@Composable
fun DriverCard(ride: RideDetail, modifier: Modifier = Modifier) {
        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                                text = "Conducteur",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                DriverAvatar(
                                        profilePictureUrl = ride.driverProfilePictureUrl,
                                        driverName = ride.driverName
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                DriverInfo(
                                        driverName = ride.driverName,
                                        driverRating = ride.driverRating
                                )
                        }
                }
        }
}

@Composable
private fun DriverAvatar(
        profilePictureUrl: String?,
        driverName: String,
        modifier: Modifier = Modifier
) {
        Box(modifier = modifier.size(56.dp), contentAlignment = Alignment.BottomEnd) {
                Box(
                        modifier =
                                Modifier.size(56.dp)
                                        .clip(CircleShape)
                                        .background(BlassaTeal.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                ) {
                        if (profilePictureUrl != null) {
                                AsyncImage(
                                        model = profilePictureUrl.replace("=s96-c", "=s400-c"),
                                        contentDescription = driverName,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                )
                        } else {
                                Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = BlassaTeal,
                                        modifier = Modifier.size(28.dp)
                                )
                        }
                }

                VerifiedBadge()
        }
}

@Composable
private fun VerifiedBadge(modifier: Modifier = Modifier) {
        Box(
                modifier = modifier.size(20.dp).background(Color(0xFF10B981), CircleShape),
                contentAlignment = Alignment.Center
        ) {
                Icon(
                        Icons.Default.Shield,
                        contentDescription = "Vérifié",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                )
        }
}

@Composable
private fun DriverInfo(driverName: String, driverRating: Double?, modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
                Text(
                        text = driverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                        if (driverRating != null && driverRating > 0) {
                                Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                        text = String.format("%.1f", driverRating),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD97706)
                                )
                        } else {
                                Text(
                                        text = "Nouveau conducteur",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                )
                        }
                        Text(" · ", color = TextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                        text = "Vérifié",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF10B981)
                                )
                        }
                }
        }
}

@Composable
fun VehicleCard(
        carMake: String?,
        carModel: String?,
        carColor: String?,
        carLicensePlate: String? = null,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant,
                                                        CircleShape
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = "${carMake ?: ""} ${carModel ?: ""}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                )
                                if (carColor != null) {
                                        Text(
                                                text = carColor,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                }
                        }

                        if (carLicensePlate != null) {
                                Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                                text = carLicensePlate,
                                                style =
                                                        MaterialTheme.typography.labelLarge.copy(
                                                                fontFamily =
                                                                        androidx.compose.ui.text
                                                                                .font.FontFamily
                                                                                .Monospace,
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                modifier =
                                                        Modifier.background(
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surface,
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                .border(
                                                                        width = 1.dp,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .outlineVariant,
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                .padding(
                                                                        horizontal = 8.dp,
                                                                        vertical = 2.dp
                                                                ),
                                                color = TextPrimary
                                        )

                                        if (carLicensePlate.contains("***")) {
                                                Text(
                                                        text = "Plaque complète 1h avant",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.padding(top = 2.dp)
                                                )
                                        }
                                }
                        }
                }
        }
}

private fun formatTime(dateTime: String): String {
        return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateTime)
                date?.let { outputFormat.format(it) } ?: "--:--"
        } catch (e: Exception) {
                "--:--"
        }
}

private fun formatFullDate(dateTime: String): String {
        return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH)
                val date = inputFormat.parse(dateTime)
                date?.let { outputFormat.format(it).replaceFirstChar { c -> c.uppercase() } } ?: ""
        } catch (e: Exception) {
                ""
        }
}

@Composable
fun SecureVehicleCard(
        carMake: String?,
        carModel: String?,
        carColor: String?,
        carLicensePlate: String,
        modifier: Modifier = Modifier
) {

        Column(
                modifier =
                        modifier.fillMaxWidth()
                                .background(
                                        Color(0xFFECFDF5),
                                        RoundedCornerShape(12.dp)
                                ) // bg-emerald-50
                                .border(
                                        1.dp,
                                        Color(0xFFD1FAE5),
                                        RoundedCornerShape(12.dp)
                                ) // border-emerald-100
                                .padding(16.dp)
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                                modifier =
                                        Modifier.background(
                                                        Color(0xFFD1FAE5),
                                                        RoundedCornerShape(6.dp)
                                                )
                                                .padding(4.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF065F46),
                                        modifier = Modifier.size(16.dp)
                                )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = "Véhicule confirmé",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B)
                        )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Text(
                                text =
                                        "${carMake ?: ""} ${carModel ?: ""} ${carColor ?: ""}".trim(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                        )

                        Column(horizontalAlignment = Alignment.End) {
                                Text(
                                        text = carLicensePlate,
                                        style =
                                                MaterialTheme.typography.labelMedium.copy(
                                                        fontFamily =
                                                                androidx.compose.ui.text.font
                                                                        .FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 1.sp
                                                ),
                                        modifier =
                                                Modifier.background(
                                                                Color(0xFFF1F5F9),
                                                                RoundedCornerShape(4.dp)
                                                        )
                                                        .border(
                                                                1.dp,
                                                                Color(0xFFE2E8F0),
                                                                RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(
                                                                horizontal = 8.dp,
                                                                vertical = 4.dp
                                                        ),
                                        color = TextPrimary
                                )

                                if (carLicensePlate.contains("***")) {
                                        Text(
                                                text = "Plaque complète 1h avant",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary,
                                                modifier = Modifier.padding(top = 4.dp),
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }
                }
        }
}

@Composable
fun BookingStatusBanner(
        status: String,
        onCancel: () -> Unit,
        isLoading: Boolean,
        modifier: Modifier = Modifier
) {

        val bgColor: Color
        val iconColor: Color
        val titleColor: Color
        val textColor: Color
        val icon: androidx.compose.ui.graphics.vector.ImageVector

        when (status) {
                "PENDING" -> {
                        bgColor = Color(0xFFFEF3C7)
                        iconColor = Color(0xFFD97706)
                        titleColor = Color(0xFF92400E)
                        textColor = Color(0xFF92400E)
                        icon = Icons.Default.CheckCircle
                }
                "REJECTED" -> {
                        bgColor = Color(0xFFFEE2E2)
                        iconColor = Color(0xFFDC2626)
                        titleColor = Color(0xFF991B1B)
                        textColor = Color(0xFF991B1B)
                        icon = Icons.Default.Cancel
                }
                "CANCELLED" -> {
                        bgColor = Color(0xFFF1F5F9)
                        iconColor = Color(0xFF475569)
                        titleColor = Color(0xFF475569)
                        textColor = Color(0xFF475569)
                        icon = Icons.Default.Cancel
                }
                else -> {
                        bgColor = Color(0xFFDCFCE7)
                        iconColor = Color(0xFF16A34A)
                        titleColor = Color(0xFF166534)
                        textColor = Color(0xFF166534)
                        icon = Icons.Default.CheckCircle
                }
        }

        val titleText =
                when (status) {
                        "PENDING" -> "Demande envoyée"
                        "REJECTED" -> "Demande refusée"
                        "CANCELLED" -> "Réservation annulée"
                        else -> "Réservation confirmée"
                }

        val bodyText =
                when (status) {
                        "PENDING" ->
                                "Votre demande a été envoyée au conducteur. Vous serez notifié dès qu'elle sera acceptée."
                        "REJECTED" ->
                                "Le conducteur a refusé votre demande de réservation pour ce trajet."
                        "CANCELLED" -> "Vous avez annulé votre réservation pour ce trajet."
                        else ->
                                "Vous avez réservé ce trajet. Toutes les informations sont affichées ci-dessous."
                }

        var showCancelDialog by remember { mutableStateOf(false) }

        Column(
                modifier = modifier.fillMaxWidth().background(bgColor).padding(24.dp), // p-6
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                // Icon
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .background(iconColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                        )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Body
                Text(
                        text = bodyText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel Action (Only for Pending/Confirmed)
                if (status == "PENDING" || status == "CONFIRMED") {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { showCancelDialog = true }) {
                                Text(
                                        text = "Annuler",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                }
        }

        // Cancellation Dialog
        if (showCancelDialog) {
                AlertDialog(
                        onDismissRequest = { showCancelDialog = false },
                        title = { Text("Annuler la réservation") },
                        text = { Text("Êtes-vous sûr de vouloir annuler votre réservation ?") },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                showCancelDialog = false
                                                onCancel()
                                        }
                                ) { Text("Oui, annuler", color = Color.Red) }
                        },
                        dismissButton = {
                                TextButton(onClick = { showCancelDialog = false }) { Text("Non") }
                        },
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        textContentColor = Color.Gray
                )
        }
}

data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
