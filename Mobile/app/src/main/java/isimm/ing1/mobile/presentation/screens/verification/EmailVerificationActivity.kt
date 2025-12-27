package isimm.ing1.mobile.presentation.screens.verification

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
class EmailVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: "user@email.com"
        enableEdgeToEdge()
        setContent {
            MobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EmailVerificationScreen(
                        paddingValues = innerPadding,
                        email = email,
                        onVerified = {
                            // TODO: Navigate to home
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
