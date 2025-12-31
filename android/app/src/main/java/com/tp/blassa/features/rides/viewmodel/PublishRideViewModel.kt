package com.tp.blassa.features.rides.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.RideRequest
import com.tp.blassa.core.network.Vehicle
import com.tp.blassa.core.network.parseErrorMessage
import com.tp.blassa.data.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PublishRideUiState(
        val originCity: City? = null,
        val destinationCity: City? = null,
        val departureDate: String = "",
        val departureTime: String = "",
        val totalSeats: Int = 3,
        val pricePerSeat: String = "",
        val allowsSmoking: Boolean = false,
        val allowsMusic: Boolean = false,
        val allowsPets: Boolean = false,
        val luggageSize: String = "MEDIUM",
        val genderPreference: String = "ANY",
        val selectedVehicle: Vehicle? = null,
        val vehicles: List<Vehicle> = emptyList(),
        val isLoadingVehicles: Boolean = true,
        val isSubmitting: Boolean = false,
        val showSuccess: Boolean = false,
        val createdRideId: String? = null,
        val snackbarMessage: String? = null
)

class PublishRideViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublishRideUiState())
    val uiState: StateFlow<PublishRideUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    fun setOriginCity(city: City?) {
        _uiState.update { it.copy(originCity = city) }
    }

    fun setDestinationCity(city: City?) {
        _uiState.update { it.copy(destinationCity = city) }
    }

    fun setDepartureDate(date: String) {
        _uiState.update { it.copy(departureDate = date) }
    }

    fun setDepartureTime(time: String) {
        _uiState.update { it.copy(departureTime = time) }
    }

    fun setTotalSeats(seats: Int) {
        if (seats in 1..8) {
            _uiState.update { it.copy(totalSeats = seats) }
        }
    }

    fun setPricePerSeat(price: String) {
        _uiState.update { it.copy(pricePerSeat = price) }
    }

    fun setAllowsSmoking(allows: Boolean) {
        _uiState.update { it.copy(allowsSmoking = allows) }
    }

    fun setGenderPreference(preference: String) {
        _uiState.update { it.copy(genderPreference = preference) }
    }

    fun setAllowsMusic(allows: Boolean) {
        _uiState.update { it.copy(allowsMusic = allows) }
    }

    fun setAllowsPets(allows: Boolean) {
        _uiState.update { it.copy(allowsPets = allows) }
    }

    fun setLuggageSize(size: String) {
        _uiState.update { it.copy(luggageSize = size) }
    }

    fun setSelectedVehicle(vehicle: Vehicle?) {
        _uiState.update { it.copy(selectedVehicle = vehicle) }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            try {
                val vehicles = RetrofitClient.dashboardApiService.getMyVehicles()
                _uiState.update { state ->
                    state.copy(
                            vehicles = vehicles,
                            isLoadingVehicles = false,
                            selectedVehicle = if (vehicles.size == 1) vehicles.first() else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                            isLoadingVehicles = false,
                            snackbarMessage = "Impossible de charger vos véhicules"
                    )
                }
            }
        }
    }

    fun validateAndSubmit() {
        val state = _uiState.value

        val validationError =
                when {
                    state.originCity == null -> "Sélectionnez une ville de départ"
                    state.destinationCity == null -> "Sélectionnez une ville d'arrivée"
                    state.originCity == state.destinationCity ->
                            "Les villes doivent être différentes"
                    state.departureDate.isEmpty() -> "Sélectionnez une date de départ"
                    state.departureTime.isEmpty() -> "Sélectionnez une heure de départ"
                    state.selectedVehicle == null -> "Sélectionnez un véhicule"
                    state.pricePerSeat.isEmpty() ||
                            state.pricePerSeat.toDoubleOrNull() == null ||
                            (state.pricePerSeat.toDoubleOrNull() ?: 0.0) <= 0 ->
                            "Entrez un prix valide"
                    else -> null
                }

        if (validationError != null) {
            _uiState.update { it.copy(snackbarMessage = validationError) }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                val departureDateTime = "${state.departureDate}T${state.departureTime}:00+01:00"

                val request =
                        RideRequest(
                                originName = state.originCity!!.name,
                                originLat = state.originCity.lat,
                                originLon = state.originCity.lon,
                                destinationName = state.destinationCity!!.name,
                                destinationLat = state.destinationCity.lat,
                                destinationLon = state.destinationCity.lon,
                                departureTime = departureDateTime,
                                totalSeats = state.totalSeats,
                                pricePerSeat = state.pricePerSeat.toDouble(),
                                allowsSmoking = state.allowsSmoking,
                                allowsMusic = state.allowsMusic,
                                allowsPets = state.allowsPets,
                                luggageSize = state.luggageSize,
                                genderPreference = state.genderPreference,
                                vehicleId = state.selectedVehicle!!.id
                        )

                val response = RetrofitClient.dashboardApiService.createRide(request)
                _uiState.update {
                    it.copy(isSubmitting = false, showSuccess = true, createdRideId = response.id)
                }
            } catch (e: retrofit2.HttpException) {
                _uiState.update {
                    it.copy(isSubmitting = false, snackbarMessage = e.parseErrorMessage())
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSubmitting = false, snackbarMessage = "Erreur lors de la publication")
                }
            }
        }
    }
}
