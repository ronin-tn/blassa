package com.tp.blassa.features.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.splash.viewmodel.SplashEvent
import com.tp.blassa.features.splash.viewmodel.SplashViewModel
import com.tp.blassa.ui.theme.BlassaTeal

@Composable
fun SplashScreen(
        onNavigateToOnboarding: () -> Unit,
        onNavigateToLogin: () -> Unit,
        onNavigateToCompleteProfile: () -> Unit,
        onNavigateToDashboard: () -> Unit,
        viewModel: SplashViewModel = viewModel()
) {
    val event by viewModel.event.collectAsState()

    LaunchedEffect(event) {
        when (event) {
            is SplashEvent.NavigateToOnboarding -> onNavigateToOnboarding()
            is SplashEvent.NavigateToLogin -> onNavigateToLogin()
            is SplashEvent.NavigateToCompleteProfile -> onNavigateToCompleteProfile()
            is SplashEvent.NavigateToDashboard -> onNavigateToDashboard()
            null -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = BlassaTeal)
    }
}
