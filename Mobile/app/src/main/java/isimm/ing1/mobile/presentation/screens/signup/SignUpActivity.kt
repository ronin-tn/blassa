package isimm.ing1.mobile.presentation.screens.signup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import isimm.ing1.mobile.ui.theme.MobileTheme

/**
 * Legacy Activity - keeping for backwards compatibility.
 * Navigation is now handled through MainActivity with NavHost.
 */
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignUpScreen(
                        paddingValues = innerPadding,
                        onSignUpSuccess = { _ ->
                            // TODO: Navigate to email verification
                            finish()
                        },
                        onGoogleSignInClick = {
                            // TODO: Implement Google Sign-In
                        },
                        onLoginClick = {
                            finish()
                        },
                        onBackClick = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}
