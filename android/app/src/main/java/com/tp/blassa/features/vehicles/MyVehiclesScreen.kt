package com.tp.blassa.features.vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.core.network.Vehicle
import com.tp.blassa.features.vehicles.viewmodel.VehiclesViewModel
import com.tp.blassa.ui.theme.BlassaRed
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVehiclesScreen(
        onBack: () -> Unit,
        onNavigateToAddVehicle: () -> Unit,
        viewModel: VehiclesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadVehicles() }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Mes Véhicules", fontWeight = FontWeight.SemiBold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        Icons.Default.ArrowBack,
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
            floatingActionButton = {
                FloatingActionButton(
                        onClick = onNavigateToAddVehicle,
                        containerColor = BlassaTeal,
                        contentColor = Color.White
                ) { Icon(Icons.Default.Add, contentDescription = "Ajouter un véhicule") }
            },
            containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BlassaTeal
                )
            } else if (uiState.vehicles.isEmpty()) {
                Text(
                        text = "Aucun véhicule enregistré",
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.vehicles) { vehicle ->
                        VehicleItem(
                                vehicle = vehicle,
                                onDelete = { viewModel.deleteVehicle(vehicle.id) }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                        containerColor = BlassaRed
                ) { Text(error, color = Color.White) }
            }
        }
    }
}

@Composable
fun VehicleItem(vehicle: Vehicle, onDelete: () -> Unit) {
    Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = "${vehicle.make} ${vehicle.model}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                )
                Text(
                        text = "Couleur: ${vehicle.color}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                )
                Text(
                        text = "Immatriculation: ${vehicle.licensePlate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = BlassaRed)
            }
        }
    }
}
