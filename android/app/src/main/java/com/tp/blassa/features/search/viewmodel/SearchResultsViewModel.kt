package com.tp.blassa.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.Ride
import com.tp.blassa.features.search.model.SearchFiltersState
import com.tp.blassa.features.search.model.SearchParams
import com.tp.blassa.features.search.model.SearchResultsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchResultsUiState())
    private val _rides = MutableStateFlow<List<Ride>>(emptyList())
    private val _filters = MutableStateFlow(SearchFiltersState.DEFAULT)

    private var currentPage = 0
    private var isLastPage = false

    val uiState: StateFlow<SearchResultsUiState> = _uiState

    val filters: StateFlow<SearchFiltersState> = _filters

    val filteredRides: StateFlow<List<Ride>> =
            combine(_rides, _filters) { rides, filters -> applyFiltersAndSort(rides, filters) }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = emptyList()
                    )

    val allRides: StateFlow<List<Ride>> = _rides

    fun initializeSearch(params: SearchParams) {
        _uiState.update { it.copy(searchParams = params) }
        loadInitialRides()
    }

    fun updateFilters(newFilters: SearchFiltersState) {
        _filters.value = newFilters
        _uiState.update { it.copy(filters = newFilters) }
    }

    fun resetFilters() {
        updateFilters(SearchFiltersState.DEFAULT)
    }

    fun loadMoreRides() {
        if (_uiState.value.isLoadingMore || isLastPage) return
        loadRides(isFirstLoad = false)
    }

    fun retry() {
        loadInitialRides()
    }

    private fun loadInitialRides() {
        currentPage = 0
        isLastPage = false
        _rides.value = emptyList()
        loadRides(isFirstLoad = true)
    }

    private fun loadRides(isFirstLoad: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    if (isFirstLoad) it.copy(isLoading = true, error = null)
                    else it.copy(isLoadingMore = true)
                }

                val params = _uiState.value.searchParams
                val apiDate = params.date?.let { "${it}T00:00:00" }

                val response =
                        RetrofitClient.dashboardApiService.searchRides(
                                originLat = params.originLat,
                                originLon = params.originLon,
                                destLat = params.destLat,
                                destLon = params.destLon,
                                departureTime = apiDate,
                                seats = params.passengers,
                                genderFilter = params.genderFilter,
                                page = currentPage,
                                size = 20
                        )

                _rides.update { currentRides ->
                    if (isFirstLoad) response.content.distinctBy { it.id }
                    else (currentRides + response.content).distinctBy { it.id }
                }

                isLastPage =
                        response.page.number >= response.page.totalPages - 1 ||
                                response.content.isEmpty()
                if (!isLastPage) currentPage++
            } catch (e: Exception) {
                if (isFirstLoad) {
                    _uiState.update { it.copy(error = "Erreur lors de la recherche") }
                }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isLoading = false, isLoadingMore = false) }
            }
        }
    }

    private fun applyFiltersAndSort(rides: List<Ride>, filters: SearchFiltersState): List<Ride> {
        var result = rides.toList()

        if (filters.timeOfDay.isNotEmpty()) {
            result =
                    result.filter { ride ->
                        try {
                            val hour = ride.departureTime.substring(11, 13).toIntOrNull() ?: 0
                            when {
                                filters.timeOfDay.contains("morning") && hour in 6..11 -> true
                                filters.timeOfDay.contains("afternoon") && hour in 12..17 -> true
                                filters.timeOfDay.contains("evening") && (hour >= 18 || hour < 6) ->
                                        true
                                else -> false
                            }
                        } catch (e: Exception) {
                            true
                        }
                    }
        }

        filters.maxPrice?.let { maxPrice -> result = result.filter { it.pricePerSeat <= maxPrice } }

        if (filters.ladiesOnly) {
            result = result.filter { it.genderPreference == "FEMALE_ONLY" }
        }

        if (filters.noSmoking) {
            result = result.filter { !it.allowsSmoking }
        }

        result =
                when (filters.sortBy) {
                    "price_asc" -> result.sortedBy { it.pricePerSeat }
                    "price_desc" -> result.sortedByDescending { it.pricePerSeat }
                    "time_asc" -> result.sortedBy { it.departureTime }
                    "time_desc" -> result.sortedByDescending { it.departureTime }
                    else -> result
                }

        return result
    }
}
