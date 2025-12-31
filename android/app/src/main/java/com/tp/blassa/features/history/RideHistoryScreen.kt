package com.tp.blassa.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.history.viewmodel.HistoryRide
import com.tp.blassa.features.history.viewmodel.RideHistoryViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHistoryScreen(
        onBack: () -> Unit,
        onNavigateToRideDetails: (String) -> Unit,
        vm: RideHistoryViewModel = viewModel()
) {
        val uiState by vm.uiState.collectAsStateWithLifecycle()
        val pullRefreshState = rememberPullToRefreshState()

        val filteredRides =
                remember(uiState.rides, uiState.selectedFilter) {
                        when (uiState.selectedFilter) {
                                "DRIVER" -> uiState.rides.filter { it.type == "driver" }
                                "PASSENGER" -> uiState.rides.filter { it.type == "passenger" }
                                else -> uiState.rides
                        }
                }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text("Historique des trajets", fontWeight = FontWeight.Bold)
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        "Retour",
                                                        tint = Color.Black
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White
                                        )
                        )
                }
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .background(Color(0xFFF8FAFC))
                ) {
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(Color.White)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                FilterChip(
                                        selected = uiState.selectedFilter == "ALL",
                                        onClick = { vm.setFilter("ALL") },
                                        label = { Text("Tous") },
                                        colors =
                                                FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = BlassaTeal,
                                                        selectedLabelColor = Color.White
                                                )
                                )
                                FilterChip(
                                        selected = uiState.selectedFilter == "DRIVER",
                                        onClick = { vm.setFilter("DRIVER") },
                                        label = { Text("Conducteur") },
                                        colors =
                                                FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = BlassaTeal,
                                                        selectedLabelColor = Color.White
                                                )
                                )
                                FilterChip(
                                        selected = uiState.selectedFilter == "PASSENGER",
                                        onClick = { vm.setFilter("PASSENGER") },
                                        label = { Text("Passager") },
                                        colors =
                                                FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = BlassaTeal,
                                                        selectedLabelColor = Color.White
                                                )
                                )
                        }

                        Divider()

                        PullToRefreshBox(
                                isRefreshing = uiState.isLoading,
                                onRefresh = { vm.refresh() },
                                state = pullRefreshState,
                                modifier = Modifier.fillMaxSize()
                        ) {
                                when {
                                        uiState.error != null -> {
                                                Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Column(
                                                                horizontalAlignment =
                                                                        Alignment.CenterHorizontally
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.ErrorOutline,
                                                                        contentDescription = null,
                                                                        tint = Color.Gray,
                                                                        modifier =
                                                                                Modifier.size(64.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        16.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        uiState.error ?: "",
                                                                        color = Color.Gray
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        16.dp
                                                                                )
                                                                )
                                                                Button(onClick = { vm.refresh() }) {
                                                                        Text("Réessayer")
                                                                }
                                                        }
                                                }
                                        }
                                        filteredRides.isEmpty() && !uiState.isLoading -> {
                                                Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Column(
                                                                horizontalAlignment =
                                                                        Alignment
                                                                                .CenterHorizontally,
                                                                modifier = Modifier.padding(32.dp)
                                                        ) {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.size(80.dp)
                                                                                        .background(
                                                                                                Color(
                                                                                                        0xFFE2E8F0
                                                                                                ),
                                                                                                CircleShape
                                                                                        ),
                                                                        contentAlignment =
                                                                                Alignment.Center
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .History,
                                                                                contentDescription =
                                                                                        null,
                                                                                tint = BlassaTeal,
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                40.dp
                                                                                        )
                                                                        )
                                                                }
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        24.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        "Aucun trajet",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleLarge,
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        "Votre historique de trajets apparaîtra ici",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        color = Color.Gray
                                                                )
                                                        }
                                                }
                                        }
                                        else -> {
                                                LazyColumn(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentPadding = PaddingValues(16.dp),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        items(
                                                                filteredRides,
                                                                key = { "${it.id}_${it.type}" }
                                                        ) { ride ->
                                                                RideHistoryCard(
                                                                        ride = ride,
                                                                        onClick = {
                                                                                onNavigateToRideDetails(
                                                                                        ride.id
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun RideHistoryCard(ride: HistoryRide, onClick: () -> Unit) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.FRENCH)
        val formattedDate =
                try {
                        dateFormat.parse(ride.departureTime)?.let { displayFormat.format(it) }
                                ?: ride.departureTime
                } catch (e: Exception) {
                        ride.departureTime
                }

        Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color =
                                                if (ride.type == "driver") Color(0xFFDCFCE7)
                                                else Color(0xFFE0F2FE)
                                ) {
                                        Row(
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 8.dp,
                                                                vertical = 4.dp
                                                        ),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        if (ride.type == "driver")
                                                                Icons.Default.DirectionsCar
                                                        else Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint =
                                                                if (ride.type == "driver")
                                                                        Color(0xFF059669)
                                                                else Color(0xFF0284C7),
                                                        modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        if (ride.type == "driver") "Conducteur"
                                                        else "Passager",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color =
                                                                if (ride.type == "driver")
                                                                        Color(0xFF059669)
                                                                else Color(0xFF0284C7)
                                                )
                                        }
                                }

                                StatusBadge(status = ride.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                                modifier =
                                                        Modifier.size(10.dp)
                                                                .clip(CircleShape)
                                                                .background(BlassaTeal)
                                        )
                                        Box(
                                                modifier =
                                                        Modifier.width(2.dp)
                                                                .height(24.dp)
                                                                .background(Color(0xFFE2E8F0))
                                        )
                                        Box(
                                                modifier =
                                                        Modifier.size(10.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFFEF4444))
                                        )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                        Text(
                                                text = ride.origin,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text = ride.destination,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        Color(0xFFF8FAFC),
                                                        RoundedCornerShape(8.dp)
                                                )
                                                .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                                text = formattedDate,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                        )
                                }

                                Text(
                                        text = "${String.format("%.1f", ride.price)} TND",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BlassaTeal
                                )
                        }

                        if (ride.type == "passenger" && !ride.driverName.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = "Conducteur: ${ride.driverName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun StatusBadge(status: String) {
        val (color, text) =
                when (status) {
                        "COMPLETED" -> Color(0xFF059669) to "Terminé"
                        "CANCELLED" -> Color(0xFFEF4444) to "Annulé"
                        "IN_PROGRESS" -> Color(0xFFF59E0B) to "En cours"
                        "SCHEDULED" -> Color(0xFF3B82F6) to "Planifié"
                        "FULL" -> Color(0xFF8B5CF6) to "Complet"
                        else -> Color.Gray to status
                }

        Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.1f)) {
                Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
        }
}
