package isimm.ing1.mobile.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import isimm.ing1.mobile.presentation.screens.login.LoginScreen
import isimm.ing1.mobile.presentation.screens.login.LoginViewModel
import isimm.ing1.mobile.presentation.screens.onboarding.OnboardingPager
import isimm.ing1.mobile.presentation.screens.onboarding.SplashScreen
import isimm.ing1.mobile.presentation.screens.password.ForgotPasswordScreen
import isimm.ing1.mobile.presentation.screens.password.ForgotPasswordViewModel
import isimm.ing1.mobile.presentation.screens.password.ResetPasswordScreen
import isimm.ing1.mobile.presentation.screens.password.ResetPasswordViewModel
import isimm.ing1.mobile.presentation.screens.signup.SignUpScreen
import isimm.ing1.mobile.presentation.screens.signup.SignUpViewModel
import isimm.ing1.mobile.presentation.screens.verification.EmailVerificationScreen
import isimm.ing1.mobile.presentation.screens.verification.EmailVerificationViewModel
import isimm.ing1.mobile.presentation.screens.verification.EmailVerifiedScreen

/**
 * Main navigation graph for the Blassa app.
 * 
 * ## Navigation Flow:
 * ```
 * Splash → Onboarding → Login ↔ SignUp
 *                         ↓
 *                   ForgotPassword → ResetPassword
 *                         ↓
 *                 EmailVerification → EmailVerified → Home
 * ```
 * 
 * ## Architecture Notes (per architecture.md):
 * - Routes are defined as constants in [NavRoutes]
 * - ViewModels are created using viewModel() composable function
 * - TODO (Backend): When Hilt is added, use hiltViewModel() instead
 * - Navigation passes IDs (email) not full objects
 * 
 * @param navController The NavHostController for navigation
 * @param paddingValues Padding from Scaffold
 * @param startDestination Starting route, defaults to Splash
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination: String = NavRoutes.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(NavRoutes.Splash.route) {
            SplashScreen {
                navController.navigate(NavRoutes.Onboarding.route) {
                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                }
            }
        }
        
        // Onboarding
        composable(NavRoutes.Onboarding.route) {
            OnboardingPager(
                onStartClick = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Login
        composable(NavRoutes.Login.route) {
            val viewModel: LoginViewModel = viewModel()
            LoginScreen(
                paddingValues = paddingValues,
                viewModel = viewModel,
                onLoginSuccess = { email ->
                    navController.navigate(NavRoutes.EmailVerification.createRoute(email))
                },
                onGoogleSignInClick = {
                    // TODO: Implement Google Sign-In
                },
                onForgotPasswordClick = {
                    navController.navigate(NavRoutes.ForgotPassword.route)
                },
                onSignUpClick = {
                    navController.navigate(NavRoutes.SignUp.route)
                }
            )
        }
        
        // Sign Up
        composable(NavRoutes.SignUp.route) {
            val viewModel: SignUpViewModel = viewModel()
            SignUpScreen(
                paddingValues = paddingValues,
                viewModel = viewModel,
                onSignUpSuccess = { email ->
                    navController.navigate(NavRoutes.EmailVerification.createRoute(email))
                },
                onGoogleSignInClick = {
                    // TODO: Implement Google Sign-In
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Forgot Password
        composable(NavRoutes.ForgotPassword.route) {
            val viewModel: ForgotPasswordViewModel = viewModel()
            ForgotPasswordScreen(
                paddingValues = paddingValues,
                viewModel = viewModel,
                onSendLinkSuccess = { email ->
                    navController.navigate(NavRoutes.ResetPassword.createRoute(email))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Reset Password
        composable(
            route = NavRoutes.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val viewModel: ResetPasswordViewModel = viewModel()
            ResetPasswordScreen(
                paddingValues = paddingValues,
                viewModel = viewModel,
                onResetSuccess = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Email Verification
        composable(
            route = NavRoutes.EmailVerification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val viewModel: EmailVerificationViewModel = viewModel()
            EmailVerificationScreen(
                paddingValues = paddingValues,
                email = email,
                viewModel = viewModel,
                onVerified = {
                    navController.navigate(NavRoutes.EmailVerified.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Email Verified
        composable(NavRoutes.EmailVerified.route) {
            EmailVerifiedScreen(
                paddingValues = paddingValues,
                onContinueClick = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.EmailVerified.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Home (placeholder)
        composable(NavRoutes.Home.route) {
            // TODO: Implement Home screen
        }
    }
}
