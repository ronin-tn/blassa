package isimm.ing1.mobile.presentation.screens.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import isimm.ing1.mobile.presentation.screens.password.ForgotPasswordActivity
import isimm.ing1.mobile.presentation.screens.signup.SignUpActivity
import isimm.ing1.mobile.presentation.screens.verification.EmailVerificationActivity
import isimm.ing1.mobile.ui.theme.MobileTheme

/**
 * Legacy Activity - keeping for backwards compatibility.
 * Navigation is now handled through MainActivity with NavHost.
 */
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        paddingValues = innerPadding,
                        onLoginSuccess = { email ->
                            val intent = Intent(this@LoginActivity, EmailVerificationActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        },
                        onGoogleSignInClick = {
                            // TODO: Implement Google Sign-In
                        },
                        onForgotPasswordClick = {
                            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                        },
                        onSignUpClick = {
                            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}
