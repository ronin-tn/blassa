package com.tp.blassa.features.vehicles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehiclesUiState(
        val vehicles: List<Vehicle> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
)

class VehiclesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VehiclesUiState())
    val uiState: StateFlow<VehiclesUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    fun loadVehicles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val vehicles = RetrofitClient.dashboardApiService.getMyVehicles()
                _uiState.update { it.copy(vehicles = vehicles, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Impossible de charger les véhicules")
                }
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                RetrofitClient.dashboardApiService.deleteVehicle(vehicleId)
                loadVehicles()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Erreur lors de la suppression")
                }
            }
        }
    }
}
