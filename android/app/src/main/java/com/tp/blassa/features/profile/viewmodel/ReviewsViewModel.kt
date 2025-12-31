package com.tp.blassa.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewsUiState(
        val reviews: List<Review> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
)

class ReviewsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response =
                        RetrofitClient.dashboardApiService.getMyReceivedReviews(
                                page = 0,
                                size = 100
                        )
                _uiState.update { it.copy(reviews = response.content, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Impossible de charger les avis")
                }
            }
        }
    }
}
