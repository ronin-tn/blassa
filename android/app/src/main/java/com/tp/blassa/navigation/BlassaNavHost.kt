package com.tp.blassa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.features.auth.EmailVerificationScreen
import com.tp.blassa.features.auth.ForgotPasswordScreen
import com.tp.blassa.features.auth.LoginScreen
import com.tp.blassa.features.auth.RegisterScreen
import com.tp.blassa.features.dashboard.DashboardScreen
import com.tp.blassa.features.history.RideHistoryScreen
import com.tp.blassa.features.onboarding.OnboardingScreen
import com.tp.blassa.features.rides.PublishRideScreen
import com.tp.blassa.features.rides.RideDetailsScreen
import com.tp.blassa.features.search.SearchResultsScreen
import com.tp.blassa.features.search.SearchRideScreen
import com.tp.blassa.features.splash.SplashScreen
import com.tp.blassa.features.vehicles.AddVehicleScreen

@Composable
fun BlassaNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = BlassaRoute.Splash.route) {
        composable(BlassaRoute.Splash.route) {
            SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(BlassaRoute.Onboarding.route) {
                            popUpTo(BlassaRoute.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(BlassaRoute.Login.route) {
                            popUpTo(BlassaRoute.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToCompleteProfile = {
                        navController.navigate(BlassaRoute.CompleteProfile.route) {
                            popUpTo(BlassaRoute.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToDashboard = {
                        navController.navigate(BlassaRoute.Dashboard.route) {
                            popUpTo(BlassaRoute.Splash.route) { inclusive = true }
                        }
                    }
            )
        }
        composable(BlassaRoute.Onboarding.route) {
            OnboardingScreen(
                    onFinish = {
                        navController.navigate(BlassaRoute.Dashboard.route) {
                            popUpTo(BlassaRoute.Onboarding.route) { inclusive = true }
                        }
                    }
            )
        }
        composable(BlassaRoute.Login.route) {
            LoginScreen(
                    onNavigateToRegister = { navController.navigate(BlassaRoute.Register.route) },
                    onNavigateToForgotPassword = {
                        navController.navigate(BlassaRoute.ForgotPassword.route)
                    },
                    onNavigateToCompleteProfile = {
                        navController.navigate(BlassaRoute.CompleteProfile.route) {
                            popUpTo(BlassaRoute.Login.route) { inclusive = true }
                        }
                    },
                    onLoginSuccess = {
                        navController.navigate(BlassaRoute.Dashboard.route) {
                            popUpTo(BlassaRoute.Login.route) { inclusive = true }
                        }
                    }
            )
        }
        composable(BlassaRoute.Register.route) {
            RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = { email ->
                        navController.navigate(BlassaRoute.EmailVerification.createRoute(email)) {
                            popUpTo(BlassaRoute.Register.route) { inclusive = true }
                        }
                    }
            )
        }
        composable(BlassaRoute.CompleteProfile.route) {
            com.tp.blassa.features.auth.CompleteProfileScreen(
                    onProfileCompleted = {
                        navController.navigate(BlassaRoute.Dashboard.route) {
                            popUpTo(BlassaRoute.CompleteProfile.route) { inclusive = true }
                        }
                    }
            )
        }
        composable(
                route = BlassaRoute.EmailVerification.route,
                arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                    email = email,
                    onNavigateToLogin = {
                        navController.navigate(BlassaRoute.Login.route) {
                            popUpTo(BlassaRoute.EmailVerification.route) { inclusive = true }
                        }
                    },
                    onVerificationComplete = {
                        navController.navigate(BlassaRoute.Dashboard.route) {
                            popUpTo(BlassaRoute.EmailVerification.route) { inclusive = true }
                        }
                    }
            )
        }
        composable(BlassaRoute.ForgotPassword.route) {
            ForgotPasswordScreen(onNavigateToLogin = { navController.popBackStack() })
        }
        composable(BlassaRoute.Dashboard.route) {
            DashboardScreen(
                    onNavigateToProfileMenu = {
                        navController.navigate(BlassaRoute.ProfileMenu.route)
                    },
                    onNavigateToPublish = {
                        if (TokenManager.getAccessToken() == null) {
                            navController.navigate(BlassaRoute.Login.route)
                        } else {
                            navController.navigate(BlassaRoute.PublishRide.route)
                        }
                    },
                    onNavigateToSearch = { navController.navigate(BlassaRoute.SearchRide.route) },
                    onNavigateToNotifications = {
                        if (TokenManager.getAccessToken() == null) {
                            navController.navigate(BlassaRoute.Login.route)
                        } else {
                            navController.navigate(BlassaRoute.Notifications.route)
                        }
                    },
                    onNavigateToRideDetails = { rideId ->
                        navController.navigate(BlassaRoute.RideDetails.createRoute(rideId))
                    },
                    onNavigateToHistory = {
                        if (TokenManager.getAccessToken() == null) {
                            navController.navigate(BlassaRoute.Login.route)
                        } else {
                            navController.navigate(BlassaRoute.RideHistory.route)
                        }
                    }
            )
        }

        composable(
                route = BlassaRoute.RideDetails.route,
                arguments = listOf(navArgument("rideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rideId = backStackEntry.arguments?.getString("rideId") ?: ""
            RideDetailsScreen(
                    rideId = rideId,
                    onBack = { navController.popBackStack() },
                    onNavigateToProfile = { userId ->
                        navController.navigate(BlassaRoute.UserProfile.createRoute(userId))
                    },
                    onNavigateToManageRides = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(BlassaRoute.Login.route) }
            )
        }

        composable(
                route = BlassaRoute.UserProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            com.tp.blassa.features.profile.DriverProfileScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() }
            )
        }

        composable(BlassaRoute.PublishRide.route) {
            PublishRideScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() },
                    onNavigateToAddVehicle = {
                        navController.navigate(BlassaRoute.AddVehicle.route)
                    }
            )
        }

        composable(BlassaRoute.AddVehicle.route) {
            AddVehicleScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
            )
        }

        composable(BlassaRoute.ProfileMenu.route) {
            com.tp.blassa.features.profile.ProfileMenuScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToEditProfile = {
                        navController.navigate(BlassaRoute.EditProfile.route)
                    },
                    onNavigateToChangePassword = {
                        navController.navigate(BlassaRoute.ChangePassword.route)
                    },
                    onNavigateToMyVehicles = {
                        navController.navigate(BlassaRoute.MyVehicles.route)
                    },
                    onNavigateToReviews = { navController.navigate(BlassaRoute.Reviews.route) },
                    onNavigateToNotifications = {
                        navController.navigate(BlassaRoute.Notifications.route)
                    },
                    onLogout = {
                        TokenManager.clearTokens()
                        navController.navigate(BlassaRoute.Login.route) {
                            popUpTo(BlassaRoute.Dashboard.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(BlassaRoute.MyVehicles.route) {
            com.tp.blassa.features.vehicles.MyVehiclesScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAddVehicle = {
                        navController.navigate(BlassaRoute.AddVehicle.route)
                    }
            )
        }

        composable(BlassaRoute.Reviews.route) {
            com.tp.blassa.features.profile.ReviewsScreen(onBack = { navController.popBackStack() })
        }
        composable(BlassaRoute.Notifications.route) {
            com.tp.blassa.features.notifications.NotificationsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToRideDetails = { rideId ->
                        navController.navigate(BlassaRoute.RideDetails.createRoute(rideId))
                    }
            )
        }

        composable(BlassaRoute.RideHistory.route) {
            RideHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToRideDetails = { rideId ->
                        navController.navigate(BlassaRoute.RideDetails.createRoute(rideId))
                    }
            )
        }

        composable(BlassaRoute.EditProfile.route) {
            com.tp.blassa.features.profile.EditProfileScreen(
                    onBack = { navController.popBackStack() }
            )
        }

        composable(BlassaRoute.ChangePassword.route) {
            com.tp.blassa.features.profile.ChangePasswordScreen(
                    onBack = { navController.popBackStack() }
            )
        }

        composable(BlassaRoute.SearchRide.route) {
            SearchRideScreen(
                    onBack = { navController.popBackStack() },
                    onSearch = { from, to, oLat, oLon, dLat, dLon, date, passengers, gender ->
                        navController.navigate(
                                BlassaRoute.SearchResults.createRoute(
                                        from = from,
                                        to = to,
                                        originLat = oLat,
                                        originLon = oLon,
                                        destLat = dLat,
                                        destLon = dLon,
                                        passengers = passengers,
                                        date = date,
                                        gender = gender
                                )
                        )
                    }
            )
        }

        composable(
                route = BlassaRoute.SearchResults.route,
                arguments =
                        listOf(
                                navArgument("from") { type = NavType.StringType },
                                navArgument("to") { type = NavType.StringType },
                                navArgument("originLat") { type = NavType.FloatType },
                                navArgument("originLon") { type = NavType.FloatType },
                                navArgument("destLat") { type = NavType.FloatType },
                                navArgument("destLon") { type = NavType.FloatType },
                                navArgument("passengers") { type = NavType.IntType },
                                navArgument("date") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument("gender") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                        )
        ) { backStackEntry ->
            val args = backStackEntry.arguments!!
            SearchResultsScreen(
                    from = args.getString("from") ?: "",
                    to = args.getString("to") ?: "",
                    originLat = args.getFloat("originLat").toDouble(),
                    originLon = args.getFloat("originLon").toDouble(),
                    destLat = args.getFloat("destLat").toDouble(),
                    destLon = args.getFloat("destLon").toDouble(),
                    date = args.getString("date"),
                    passengers = args.getInt("passengers"),
                    genderFilter = args.getString("gender"),
                    onBack = { navController.popBackStack() },
                    onRideClick = { rideId ->
                        navController.navigate(BlassaRoute.RideDetails.createRoute(rideId))
                    }
            )
        }
    }
}
