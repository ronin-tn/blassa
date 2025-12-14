package com.blassa.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blassa.presentation.screens.auth.login.LoginScreen
import com.blassa.presentation.screens.auth.register.RegisterScreen
import com.blassa.presentation.screens.onboarding.OnboardingScreen
import com.blassa.presentation.screens.splash.SplashScreen

@Composable
fun BlassaNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        // Splash Screen
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Route.Onboarding) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding
        composable<Route.Onboarding> {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        
        // Login
        composable<Route.Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.ForgotPassword)
                },
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToEmailVerification = {
                    navController.navigate(Route.EmailVerification)
                }
            )
        }
        
        // Register
        composable<Route.Register> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Route.EmailVerification) {
                        popUpTo(Route.Register) { inclusive = true }
                    }
                }
            )
        }
        
        // TODO: Add remaining screens as we implement them
        // Home, Search, Ride Details, etc.
    }
}
