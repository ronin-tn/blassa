package com.tp.blassa.features.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.core.network.Ride
import com.tp.blassa.features.search.model.SearchParams
import com.tp.blassa.features.search.viewmodel.SearchResultsViewModel
import com.tp.blassa.ui.components.LoadingScreen
import com.tp.blassa.ui.components.RideCard
import com.tp.blassa.ui.theme.Background
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
        from: String,
        to: String,
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        date: String?,
        passengers: Int,
        genderFilter: String?,
        onBack: () -> Unit,
        onRideClick: (String) -> Unit,
        viewModel: SearchResultsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredRides by viewModel.filteredRides.collectAsStateWithLifecycle()
    val allRides by viewModel.allRides.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    var showFilters by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.initializeSearch(
                SearchParams(
                        from = from,
                        to = to,
                        originLat = originLat,
                        originLon = originLon,
                        destLat = destLat,
                        destLon = destLon,
                        date = date,
                        passengers = passengers,
                        genderFilter = genderFilter
                )
        )
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - 2)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreRides()
        }
    }

    Scaffold(
            topBar = {
                SearchResultsTopBar(
                        from = from,
                        to = to,
                        date = date,
                        passengers = passengers,
                        activeFiltersCount = filters.activeFiltersCount,
                        onBack = onBack,
                        onFilterClick = { showFilters = true }
                )
            },
            containerColor = Background
    ) { padding ->
        SearchResultsContent(
                isLoading = uiState.isLoading,
                isLoadingMore = uiState.isLoadingMore,
                error = uiState.error,
                allRides = allRides,
                filteredRides = filteredRides,
                listState = listState,
                padding = padding,
                onRideClick = onRideClick,
                onResetFilters = viewModel::resetFilters,
                onRetry = viewModel::retry
        )
    }

    if (showFilters) {
        SearchFiltersSheet(
                filters = filters,
                onFiltersChange = viewModel::updateFilters,
                onDismiss = { showFilters = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultsTopBar(
        from: String,
        to: String,
        date: String?,
        passengers: Int,
        activeFiltersCount: Int,
        onBack: () -> Unit,
        onFilterClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    TopAppBar(
            title = {
                Column {
                    Text(
                            text = "$from → $to",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = "${date ?: "Date non spécifiée"} • $passengers pers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                }
            },
            actions = {
                FilterBadgeButton(activeFiltersCount = activeFiltersCount, onClick = onFilterClick)
            },
            colors =
                    TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = TextPrimary
                    ),
            modifier = modifier
    )
}

@Composable
private fun FilterBadgeButton(
        activeFiltersCount: Int,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    BadgedBox(
            badge = {
                if (activeFiltersCount > 0) {
                    Badge(containerColor = BlassaTeal, contentColor = Color.White) {
                        Text(activeFiltersCount.toString())
                    }
                }
            },
            modifier = modifier
    ) {
        IconButton(onClick = onClick) {
            Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filtres",
                    tint = if (activeFiltersCount > 0) BlassaTeal else TextSecondary
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
        isLoading: Boolean,
        isLoadingMore: Boolean,
        error: String?,
        allRides: List<Ride>,
        filteredRides: List<Ride>,
        listState: androidx.compose.foundation.lazy.LazyListState,
        padding: PaddingValues,
        onRideClick: (String) -> Unit,
        onResetFilters: () -> Unit,
        onRetry: () -> Unit,
        modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            LoadingScreen("Recherche des trajets...")
        }
        error != null -> {
            ErrorState(error = error, padding = padding, onRetry = onRetry)
        }
        allRides.isEmpty() -> {
            EmptySearchState(padding = padding)
        }
        filteredRides.isEmpty() -> {
            NoFilterResultsState(padding = padding, onResetFilters = onResetFilters)
        }
        else -> {
            RidesList(
                    rides = filteredRides,
                    totalRidesCount = allRides.size,
                    isLoadingMore = isLoadingMore,
                    listState = listState,
                    padding = padding,
                    onRideClick = onRideClick,
                    modifier = modifier
            )
        }
    }
}

@Composable
private fun ErrorState(
        error: String,
        padding: PaddingValues,
        onRetry: () -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Réessayer") }
        }
    }
}

@Composable
private fun EmptySearchState(padding: PaddingValues, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                    text = "Aucun trajet trouvé",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
            )
            Text(
                    text = "Essayez d'autres dates ou destinations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
            )
        }
    }
}

@Composable
private fun NoFilterResultsState(
        padding: PaddingValues,
        onResetFilters: () -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                    text = "Aucun résultat avec ces filtres",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "Essayez de modifier vos critères.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onResetFilters) { Text("Réinitialiser les filtres") }
        }
    }
}

@Composable
private fun RidesList(
        rides: List<Ride>,
        totalRidesCount: Int,
        isLoadingMore: Boolean,
        listState: androidx.compose.foundation.lazy.LazyListState,
        padding: PaddingValues,
        onRideClick: (String) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") {
            val countText = buildString {
                append("${rides.size} trajet")
                if (rides.size > 1) append("s")
                if (rides.size != totalRidesCount) append(" (sur $totalRidesCount)")
            }
            Text(
                    text = countText,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
            )
        }

        itemsIndexed(items = rides, key = { index, ride -> "${index}_${ride.id}" }) { _, ride ->
            RideCard(ride = ride, onClick = { onRideClick(ride.id) })
        }

        if (isLoadingMore) {
            item(key = "loading") { LoadingMoreIndicator() }
        }
    }
}

@Composable
private fun LoadingMoreIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = TextSecondary
        )
    }
}
