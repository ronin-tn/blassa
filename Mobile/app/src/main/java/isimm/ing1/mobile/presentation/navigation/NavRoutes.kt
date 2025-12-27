package isimm.ing1.mobile.presentation.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class NavRoutes(val route: String) {
    // Onboarding Flow
    object Splash : NavRoutes("splash")
    object Onboarding : NavRoutes("onboarding")
    
    // Auth Flow
    object Login : NavRoutes("login")
    object SignUp : NavRoutes("signup")
    object ForgotPassword : NavRoutes("forgot_password")
    object ResetPassword : NavRoutes("reset_password/{email}") {
        fun createRoute(email: String) = "reset_password/$email"
    }
    object EmailVerification : NavRoutes("email_verification/{email}") {
        fun createRoute(email: String) = "email_verification/$email"
    }
    object EmailVerified : NavRoutes("email_verified")
    
    // Main App
    object Home : NavRoutes("home")
}
