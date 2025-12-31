package com.tp.blassa.features.search.model

data class SearchFiltersState(
        val sortBy: String = "",
        val timeOfDay: Set<String> = emptySet(),
        val maxPrice: Int? = null,
        val ladiesOnly: Boolean = false,
        val noSmoking: Boolean = false,
        val allowsMusic: Boolean = false,
        val allowsPets: Boolean = false,
        val luggageSize: String = ""
) {
        val hasActiveFilters: Boolean
                get() =
                        sortBy.isNotEmpty() ||
                                timeOfDay.isNotEmpty() ||
                                maxPrice != null ||
                                ladiesOnly ||
                                noSmoking ||
                                allowsMusic ||
                                allowsPets ||
                                luggageSize.isNotEmpty()

        val activeFiltersCount: Int
                get() =
                        listOf(
                                        sortBy.isNotEmpty(),
                                        timeOfDay.isNotEmpty(),
                                        maxPrice != null,
                                        ladiesOnly,
                                        noSmoking,
                                        allowsMusic,
                                        allowsPets,
                                        luggageSize.isNotEmpty()
                                )
                                .count { it }

        companion object {
                val DEFAULT = SearchFiltersState()
        }
}

data class SearchResultsUiState(
        val isLoading: Boolean = true,
        val isLoadingMore: Boolean = false,
        val error: String? = null,
        val searchParams: SearchParams = SearchParams(),
        val filters: SearchFiltersState = SearchFiltersState.DEFAULT
)

data class SearchParams(
        val from: String = "",
        val to: String = "",
        val originLat: Double = 0.0,
        val originLon: Double = 0.0,
        val destLat: Double = 0.0,
        val destLon: Double = 0.0,
        val date: String? = null,
        val passengers: Int = 1,
        val genderFilter: String? = null
)
