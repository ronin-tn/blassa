package com.tp.blassa.features.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.R
import com.tp.blassa.features.dashboard.components.EmptyRidesCard
import com.tp.blassa.features.dashboard.components.FilterChipRow
import com.tp.blassa.features.dashboard.components.RideCard
import com.tp.blassa.features.dashboard.components.StatsGrid
import com.tp.blassa.features.dashboard.viewmodel.DashboardViewModel
import com.tp.blassa.ui.components.LoadingScreen
import com.tp.blassa.ui.theme.Background
import com.tp.blassa.ui.theme.BlassaOrange
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
        onNavigateToProfileMenu: () -> Unit,
        onNavigateToPublish: () -> Unit = {},
        onNavigateToSearch: () -> Unit = {},
        onNavigateToNotifications: () -> Unit = {},
        onNavigateToRideDetails: (String) -> Unit = {},
        onNavigateToHistory: () -> Unit = {},
        viewModel: DashboardViewModel = viewModel()
) {
        val uiState by viewModel.uiState.collectAsState()
        val filteredRides = viewModel.getFilteredRides()
        val isNewUser =
                uiState.stats.totalTrips == 0 &&
                        uiState.stats.totalRides == 0 &&
                        uiState.upcomingRides.isEmpty()

        // Auto-refresh when screen becomes visible (on return from other screens)
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        android.util.Log.d(
                                "DashboardScreen",
                                "Screen resumed - refreshing data silently"
                        )
                        viewModel.refresh()
                }
        }

        val pullRefreshState = rememberPullToRefreshState()
        // Determine legitimate loading state vs refresh state
        // Initial load: isLoading AND no data
        val isInitialLoading =
                uiState.isLoading &&
                        uiState.upcomingRides.isEmpty() &&
                        uiState.stats.totalRides == 0

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                        painter =
                                                                painterResource(
                                                                        id = R.drawable.logo
                                                                ),
                                                        contentDescription = "Blassa Logo",
                                                        modifier = Modifier.size(32.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        "Blassa",
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )
                                        }
                                },
                                actions = {
                                        IconButton(onClick = onNavigateToNotifications) {
                                                BadgedBox(
                                                        badge = {
                                                                if (uiState.unreadNotificationsCount >
                                                                                0
                                                                ) {
                                                                        Badge {
                                                                                Text(
                                                                                        "${uiState.unreadNotificationsCount}"
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                ) {
                                                        Icon(
                                                                Icons.Default.Notifications,
                                                                contentDescription =
                                                                        "Notifications",
                                                                tint = TextSecondary
                                                        )
                                                }
                                        }

                                        IconButton(onClick = onNavigateToProfileMenu) {
                                                Icon(
                                                        Icons.Default.Person,
                                                        contentDescription = "Profile",
                                                        tint = TextSecondary
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White
                                        )
                        )
                },
                bottomBar = {
                        NavigationBar(containerColor = Color.White) {
                                NavigationBarItem(
                                        selected = uiState.selectedTab == 0,
                                        onClick = { viewModel.setSelectedTab(0) },
                                        icon = {
                                                Icon(
                                                        Icons.Default.Home,
                                                        contentDescription = "Accueil"
                                                )
                                        },
                                        label = { Text("Accueil") },
                                        colors =
                                                NavigationBarItemDefaults.colors(
                                                        selectedIconColor = BlassaTeal,
                                                        selectedTextColor = BlassaTeal,
                                                        indicatorColor =
                                                                BlassaTeal.copy(alpha = 0.1f)
                                                )
                                )
                                NavigationBarItem(
                                        selected = uiState.selectedTab == 1,
                                        onClick = {
                                                viewModel.setSelectedTab(1)
                                                onNavigateToSearch()
                                        },
                                        icon = {
                                                Icon(
                                                        Icons.Default.Search,
                                                        contentDescription = "Rechercher"
                                                )
                                        },
                                        label = { Text("Rechercher") },
                                        colors =
                                                NavigationBarItemDefaults.colors(
                                                        selectedIconColor = BlassaTeal,
                                                        selectedTextColor = BlassaTeal,
                                                        indicatorColor =
                                                                BlassaTeal.copy(alpha = 0.1f)
                                                )
                                )
                                NavigationBarItem(
                                        selected = uiState.selectedTab == 2,
                                        onClick = {
                                                viewModel.setSelectedTab(2)
                                                onNavigateToPublish()
                                        },
                                        icon = {
                                                Icon(
                                                        Icons.Default.Add,
                                                        contentDescription = "Publier"
                                                )
                                        },
                                        label = { Text("Publier") },
                                        colors =
                                                NavigationBarItemDefaults.colors(
                                                        selectedIconColor = BlassaTeal,
                                                        selectedTextColor = BlassaTeal,
                                                        indicatorColor =
                                                                BlassaTeal.copy(alpha = 0.1f)
                                                )
                                )
                        }
                },
                containerColor = Background
        ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        if (isInitialLoading) {
                                LoadingScreen()
                        } else {
                                PullToRefreshBox(
                                        isRefreshing = uiState.isLoading,
                                        onRefresh = { viewModel.refresh() },
                                        state = pullRefreshState,
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        LazyColumn(
                                                modifier = Modifier.fillMaxSize(),
                                                contentPadding = PaddingValues(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                if (isNewUser && !uiState.isLoading) {
                                                        item(key = "welcome") {
                                                                WelcomeCard(
                                                                        onPublishClick =
                                                                                onNavigateToPublish,
                                                                        onSearchClick =
                                                                                onNavigateToSearch
                                                                )
                                                        }
                                                } else {

                                                        item(key = "stats") {
                                                                StatsGrid(
                                                                        stats = uiState.stats,
                                                                        onStatClick = {}
                                                                )
                                                        }
                                                }

                                                item(key = "rides_header") {
                                                        Column {
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )
                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .SpaceBetween,
                                                                        verticalAlignment =
                                                                                Alignment
                                                                                        .CenterVertically
                                                                ) {
                                                                        Text(
                                                                                text =
                                                                                        "Prochains trajets",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .titleMedium,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .SemiBold,
                                                                                color = TextPrimary
                                                                        )
                                                                        androidx.compose.material3
                                                                                .TextButton(
                                                                                        onClick =
                                                                                                onNavigateToHistory
                                                                                ) {
                                                                                        Text(
                                                                                                "Voir l'historique",
                                                                                                color =
                                                                                                        BlassaTeal,
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .bodyMedium
                                                                                        )
                                                                                }
                                                                }
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                )
                                                                FilterChipRow(
                                                                        selectedFilter =
                                                                                uiState.selectedFilter,
                                                                        onFilterChange = {
                                                                                viewModel.setFilter(
                                                                                        it
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }

                                                if (filteredRides.isEmpty()) {
                                                        item(key = "empty_rides") {
                                                                EmptyRidesCard(
                                                                        onSearchClick =
                                                                                onNavigateToSearch
                                                                )
                                                        }
                                                } else {
                                                        itemsIndexed(
                                                                items = filteredRides,
                                                                key = { index, ride ->
                                                                        "${index}_${ride.id}"
                                                                }
                                                        ) { _, ride ->
                                                                RideCard(
                                                                        ride = ride,
                                                                        onClick = {
                                                                                onNavigateToRideDetails(
                                                                                        ride.id
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }

                                                item(key = "bottom_spacing") {
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun WelcomeCard(onPublishClick: () -> Unit, onSearchClick: () -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Box(
                                modifier =
                                        Modifier.size(80.dp)
                                                .clip(CircleShape)
                                                .background(BlassaTeal.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                        ) {
                                Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = "Blassa Logo",
                                        modifier = Modifier.size(48.dp)
                                )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                                text = "Bienvenue sur Blassa !",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = "Partagez vos trajets ou trouvez un covoiturage",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Button(
                                        onClick = onSearchClick,
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = BlassaOrange
                                                ),
                                        shape = RoundedCornerShape(12.dp)
                                ) {
                                        Icon(
                                                Icons.Default.Search,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Rechercher", fontWeight = FontWeight.Medium)
                                }
                                Button(
                                        onClick = onPublishClick,
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color.White,
                                                        contentColor = BlassaTeal
                                                ),
                                        border = BorderStroke(1.dp, BlassaTeal),
                                        shape = RoundedCornerShape(12.dp)
                                ) {
                                        Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Publier", fontWeight = FontWeight.Medium)
                                }
                        }
                }
        }
}
