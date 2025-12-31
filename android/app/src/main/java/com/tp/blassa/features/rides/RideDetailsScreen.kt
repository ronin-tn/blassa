package com.tp.blassa.features.rides

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.rides.components.ActiveDriverView
import com.tp.blassa.features.rides.components.ActivePassengerView
import com.tp.blassa.features.rides.components.BookingActions
import com.tp.blassa.features.rides.components.BookingStatusBanner
import com.tp.blassa.features.rides.components.CompletedDriverView
import com.tp.blassa.features.rides.components.CompletedPassengerView
import com.tp.blassa.features.rides.components.DriverContactCard
import com.tp.blassa.features.rides.components.DriverOwnRideView
import com.tp.blassa.features.rides.components.ReportDialog
import com.tp.blassa.features.rides.components.RouteHeaderCard
import com.tp.blassa.features.rides.components.SecureVehicleCard
import com.tp.blassa.features.rides.components.VehicleCard
import com.tp.blassa.features.rides.viewmodel.RideDetailsViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailsScreen(
        rideId: String,
        onBack: () -> Unit,
        onNavigateToProfile: (String) -> Unit = {},
        onNavigateToManageRides: () -> Unit = {},
        onNavigateToLogin: () -> Unit = {},
        viewModel: RideDetailsViewModel = viewModel()
) {
        val context = LocalContext.current
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(rideId) {
                viewModel.initialize(context)
                viewModel.loadRideDetails(rideId)
        }

        val bookingStatus = uiState.myBooking?.status
        val isBooked =
                uiState.myBooking != null &&
                        (bookingStatus == "PENDING" || bookingStatus == "CONFIRMED")
        val myBookingId = uiState.myBooking?.id

        var showReportDialog by remember { mutableStateOf(false) }
        var isReportLoading by remember { mutableStateOf(false) }

        when {
                uiState.isLoading -> {
                        LoadingState()
                }
                uiState.error != null -> {
                        ErrorState(
                                error = uiState.error!!,
                                onBack = onBack,
                                onRetry = { viewModel.retry(rideId) }
                        )
                }
                uiState.ride != null -> {
                        val ride = uiState.ride!!

                        Scaffold(
                                topBar = {
                                        TopAppBar(
                                                title = {
                                                        Text(
                                                                text =
                                                                        if (uiState.isOwnRide)
                                                                                "Mon trajet"
                                                                        else "Détails du trajet",
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                                },
                                                navigationIcon = {
                                                        IconButton(onClick = onBack) {
                                                                Icon(
                                                                        Icons.Default.ArrowBack,
                                                                        contentDescription =
                                                                                "Retour",
                                                                        tint = Color.Black
                                                                )
                                                        }
                                                },
                                                actions = {
                                                        if (!uiState.isOwnRide &&
                                                                        uiState.ride != null &&
                                                                        uiState.currentUserEmail !=
                                                                                null
                                                        ) {
                                                                IconButton(
                                                                        onClick = {
                                                                                showReportDialog =
                                                                                        true
                                                                        }
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default.Flag,
                                                                                contentDescription =
                                                                                        "Signaler",
                                                                                tint = Color.Gray
                                                                        )
                                                                }
                                                        }
                                                },
                                                colors =
                                                        TopAppBarDefaults.topAppBarColors(
                                                                containerColor = Color.White,
                                                                titleContentColor = TextPrimary
                                                        )
                                        )
                                },
                                containerColor = Color(0xFFF8FAFC)
                        ) { padding ->
                                Column(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .padding(padding)
                                                        .verticalScroll(rememberScrollState())
                                                        .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        RouteHeaderCard(ride = ride)

                                        if (ride.status == "CANCELLED") {
                                                CancelledBanner()
                                        } else if (uiState.isOwnRide) {
                                                when (ride.status) {
                                                        "COMPLETED" -> {
                                                                LaunchedEffect(Unit) {
                                                                        val bookingIds =
                                                                                uiState.passengers
                                                                                        .map {
                                                                                                it.bookingId
                                                                                        }
                                                                        viewModel
                                                                                .checkExistingReviews(
                                                                                        bookingIds
                                                                                )
                                                                }

                                                                CompletedDriverView(
                                                                        ride = ride,
                                                                        passengers =
                                                                                uiState.passengers,
                                                                        reviewedPassengers =
                                                                                uiState.reviewedPassengers,
                                                                        onSubmitReview = {
                                                                                bookingId,
                                                                                rating,
                                                                                comment ->
                                                                                viewModel
                                                                                        .submitReview(
                                                                                                bookingId,
                                                                                                rating,
                                                                                                comment
                                                                                        )
                                                                        },
                                                                        onBackToHome = onBack,
                                                                        onPublishNewRide =
                                                                                onNavigateToManageRides,
                                                                        isReviewSubmitting =
                                                                                uiState.isReviewSubmitting,
                                                                        onPassengerProfileClick = {
                                                                                passengerId ->
                                                                                onNavigateToProfile(
                                                                                        passengerId
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                        "IN_PROGRESS" -> {
                                                                ActiveDriverView(
                                                                        ride = ride,
                                                                        passengers =
                                                                                uiState.passengers,
                                                                        onComplete = {
                                                                                viewModel
                                                                                        .completeRide(
                                                                                                rideId
                                                                                        )
                                                                        },
                                                                        onCancel = {
                                                                                viewModel
                                                                                        .cancelRide(
                                                                                                rideId
                                                                                        )
                                                                        },
                                                                        isLoading =
                                                                                uiState.isActionLoading
                                                                )
                                                        }
                                                        else -> {
                                                                DriverOwnRideView(
                                                                        ride = ride,
                                                                        passengers =
                                                                                uiState.passengers,
                                                                        isLoading =
                                                                                uiState.isActionLoading,
                                                                        onStartRide = {
                                                                                viewModel.startRide(
                                                                                        rideId
                                                                                )
                                                                        },
                                                                        onCancelRide = {
                                                                                viewModel
                                                                                        .cancelRide(
                                                                                                rideId
                                                                                        )
                                                                        },
                                                                        onCompleteRide = {
                                                                                viewModel
                                                                                        .completeRide(
                                                                                                rideId
                                                                                        )
                                                                        },
                                                                        onManageRides =
                                                                                onNavigateToManageRides,
                                                                        onAcceptBooking = {
                                                                                bookingId ->
                                                                                viewModel
                                                                                        .acceptBooking(
                                                                                                bookingId,
                                                                                                rideId
                                                                                        )
                                                                        },
                                                                        onRejectBooking = {
                                                                                bookingId ->
                                                                                viewModel
                                                                                        .rejectBooking(
                                                                                                bookingId,
                                                                                                rideId
                                                                                        )
                                                                        }
                                                                )
                                                        }
                                                }

                                                if (ride.status != "COMPLETED" &&
                                                                ride.carMake != null
                                                ) {
                                                        VehicleCard(
                                                                carMake = ride.carMake,
                                                                carModel = ride.carModel,
                                                                carColor = ride.carColor,
                                                                carLicensePlate =
                                                                        ride.carLicensePlate
                                                        )
                                                }
                                        } else {
                                                val currentBooking = uiState.myBooking

                                                when {
                                                        ride.status == "COMPLETED" &&
                                                                currentBooking != null -> {
                                                                LaunchedEffect(Unit) {
                                                                        viewModel
                                                                                .checkExistingReviews(
                                                                                        listOf(
                                                                                                currentBooking
                                                                                                        .id
                                                                                        )
                                                                                )
                                                                }

                                                                CompletedPassengerView(
                                                                        ride = ride,
                                                                        bookingId =
                                                                                currentBooking.id,
                                                                        onSubmitReview = {
                                                                                bookingId,
                                                                                rating,
                                                                                comment ->
                                                                                viewModel
                                                                                        .submitReview(
                                                                                                bookingId,
                                                                                                rating,
                                                                                                comment
                                                                                        )
                                                                        },
                                                                        onBackToHome = onBack,
                                                                        isReviewSubmitting =
                                                                                uiState.isReviewSubmitting ==
                                                                                        currentBooking
                                                                                                .id,
                                                                        isReviewSubmitted =
                                                                                uiState.isReviewSubmitted,
                                                                        hasExistingReview =
                                                                                uiState.hasExistingReview,
                                                                        onDriverProfileClick = {
                                                                                onNavigateToProfile(
                                                                                        ride.driverId
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                        ride.status == "IN_PROGRESS" &&
                                                                isBooked -> {
                                                                ActivePassengerView(
                                                                        ride = ride,
                                                                        onDriverProfileClick = {
                                                                                onNavigateToProfile(
                                                                                        ride.driverId
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                        else -> {
                                                                DriverContactCard(
                                                                        ride = ride,
                                                                        onDriverProfileClick = {
                                                                                onNavigateToProfile(
                                                                                        ride.driverId
                                                                                )
                                                                        }
                                                                )

                                                                if (bookingStatus == "CONFIRMED" &&
                                                                                currentBooking
                                                                                        ?.carLicensePlate !=
                                                                                        null
                                                                ) {
                                                                        SecureVehicleCard(
                                                                                carMake =
                                                                                        ride.carMake,
                                                                                carModel =
                                                                                        ride.carModel,
                                                                                carColor =
                                                                                        ride.carColor,
                                                                                carLicensePlate =
                                                                                        currentBooking
                                                                                                .carLicensePlate
                                                                        )
                                                                } else if (ride.carMake != null) {
                                                                        VehicleCard(
                                                                                carMake =
                                                                                        ride.carMake,
                                                                                carModel =
                                                                                        ride.carModel,
                                                                                carColor =
                                                                                        ride.carColor
                                                                        )
                                                                }

                                                                if (isBooked) {
                                                                        BookingStatusBanner(
                                                                                status =
                                                                                        bookingStatus
                                                                                                ?: "UNKNOWN",
                                                                                onCancel = {
                                                                                        myBookingId
                                                                                                ?.let {
                                                                                                        bookingId
                                                                                                        ->
                                                                                                        viewModel
                                                                                                                .cancelBooking(
                                                                                                                        bookingId,
                                                                                                                        rideId
                                                                                                                )
                                                                                                }
                                                                                },
                                                                                isLoading =
                                                                                        uiState.isActionLoading
                                                                        )
                                                                } else if (ride.status ==
                                                                                "SCHEDULED" &&
                                                                                ride.availableSeats >
                                                                                        0
                                                                ) {
                                                                        BookingActions(
                                                                                ride = ride,
                                                                                onBook = { seats ->
                                                                                        if (uiState.currentUserEmail ==
                                                                                                        null
                                                                                        ) {
                                                                                                onNavigateToLogin()
                                                                                        } else {
                                                                                                viewModel
                                                                                                        .bookRide(
                                                                                                                rideId,
                                                                                                                seats
                                                                                                        )
                                                                                        }
                                                                                },
                                                                                isBookingLoading =
                                                                                        uiState.isActionLoading
                                                                        )
                                                                } else if (ride.availableSeats == 0
                                                                ) {
                                                                        SoldOutBanner()
                                                                }
                                                        }
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(32.dp))
                                }
                        }
                }
        }

        // Report Dialog
        ReportDialog(
                isOpen = showReportDialog,
                onDismiss = { showReportDialog = false },
                onSubmit = { reason: String, description: String ->
                        isReportLoading = true
                        viewModel.submitReport(
                                rideId = rideId,
                                reason = reason,
                                description = description,
                                onSuccess = {
                                        isReportLoading = false
                                        showReportDialog = false
                                },
                                onError = { isReportLoading = false }
                        )
                },
                isLoading = isReportLoading
        )
}

@Composable
private fun SoldOutBanner() {
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                "Ce trajet est complet",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                        )
                }
        }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BlassaTeal)
        }
}

@Composable
private fun ErrorState(
        error: String,
        onBack: () -> Unit,
        onRetry: () -> Unit,
        modifier: Modifier = Modifier
) {
        Column(
                modifier = modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Text(text = error, color = Color.Red, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onBack) { Text("Retour") }
                        Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                        ) { Text("Réessayer") }
                }
        }
}

@Composable
private fun CancelledBanner(modifier: Modifier = Modifier) {
        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
        ) {
                Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Icon(
                                Icons.Default.Block,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                                text = "Trajet annulé",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = "Ce trajet a été annulé par le conducteur.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFDC2626),
                                textAlign = TextAlign.Center
                        )
                }
        }
}
