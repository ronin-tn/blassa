package com.tp.blassa.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.core.network.Notification
import com.tp.blassa.features.notifications.viewmodel.NotificationsViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
        onBack: () -> Unit,
        onNavigateToRideDetails: (String) -> Unit = {},
        viewModel: NotificationsViewModel = viewModel()
) {
        val uiState by viewModel.uiState.collectAsState()

        // Initial load only triggers if list is empty
        val isInitialLoading = uiState.isLoading && uiState.notifications.isEmpty()
        val pullRefreshState = rememberPullToRefreshState()

        LaunchedEffect(Unit) { viewModel.loadNotifications() }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Notifications", fontWeight = FontWeight.SemiBold) },
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
                                        ),
                                actions = {
                                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                                                Icon(
                                                        Icons.Default.Check,
                                                        contentDescription =
                                                                "Tout marquer comme lu",
                                                        tint = BlassaTeal
                                                )
                                        }
                                }
                        )
                },
                containerColor = Color(0xFFF8FAFC)
        ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        if (isInitialLoading) {
                                CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        color = BlassaTeal
                                )
                        } else {
                                PullToRefreshBox(
                                        isRefreshing = uiState.isLoading,
                                        onRefresh = { viewModel.loadNotifications() },
                                        state = pullRefreshState,
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        if (uiState.notifications.isEmpty()) {
                                                Column(
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .verticalScroll(
                                                                                rememberScrollState()
                                                                        ),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        Icon(
                                                                Icons.Default.Notifications,
                                                                contentDescription = null,
                                                                tint =
                                                                        TextSecondary.copy(
                                                                                alpha = 0.5f
                                                                        ),
                                                                modifier = Modifier.size(64.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                        Text(
                                                                text = "Aucune notification",
                                                                color = TextSecondary
                                                        )
                                                }
                                        } else {
                                                LazyColumn(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentPadding = PaddingValues(16.dp),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        items(
                                                                items = uiState.notifications,
                                                                key = { it.id }
                                                        ) { notification ->
                                                                NotificationItem(
                                                                        notification = notification,
                                                                        onMarkAsRead = {
                                                                                viewModel
                                                                                        .markAsRead(
                                                                                                notification
                                                                                                        .id
                                                                                        )
                                                                        },
                                                                        onClick = {
                                                                                // Logic to handle
                                                                                // different
                                                                                // notification
                                                                                // types
                                                                                if (notification
                                                                                                .link !=
                                                                                                null
                                                                                ) {
                                                                                        val rideId =
                                                                                                extractRideIdFromLink(
                                                                                                        notification
                                                                                                                .link
                                                                                                )
                                                                                        if (rideId !=
                                                                                                        null
                                                                                        ) {
                                                                                                onNavigateToRideDetails(
                                                                                                        rideId
                                                                                                )
                                                                                        }
                                                                                }
                                                                                viewModel
                                                                                        .markAsRead(
                                                                                                notification
                                                                                                        .id
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
fun NotificationItem(notification: Notification, onMarkAsRead: () -> Unit, onClick: () -> Unit) {
        Card(
                onClick = onClick,
                colors =
                        CardDefaults.cardColors(
                                containerColor =
                                        if (notification.isRead) Color.White
                                        else Color(0xFFE0F2F1) // Light teal for unread
                        ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
        ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Box(
                                modifier =
                                        Modifier.size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                        if (notification.isRead)
                                                                Color.Gray.copy(alpha = 0.1f)
                                                        else BlassaTeal.copy(alpha = 0.2f)
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = if (notification.isRead) Color.Gray else BlassaTeal,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = notification.title,
                                        fontWeight =
                                                if (notification.isRead) FontWeight.Normal
                                                else FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = notification.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text = notification.createdAt,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary.copy(alpha = 0.7f)
                                )
                        }

                        if (!notification.isRead) {
                                IconButton(onClick = onMarkAsRead) {
                                        Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Marquer comme lu",
                                                tint = BlassaTeal,
                                                modifier = Modifier.size(20.dp)
                                        )
                                }
                        }
                }
        }
}

private fun extractRideIdFromLink(link: String?): String? {
        if (link == null) return null
        // Example link: "/rides/123-abc"
        return if (link.contains("rides/")) {
                link.substringAfter("rides/").takeWhile { it != '/' }
        } else {
                null
        }
}
